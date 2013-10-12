package uk.thecodingbadgers.minekart.race;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import uk.thecodingbadgers.minekart.MineKart;
import uk.thecodingbadgers.minekart.jockey.Jockey;
import uk.thecodingbadgers.minekart.racecourse.Racecourse;

/**
 * @author TheCodingBadgers
 *
 * The interface for races.
 *
 */
public abstract class Race {
	
	/** The course that is used by this race */
	protected Racecourse course = null;
	
	/** The current state of the race */
	protected RaceState state = RaceState.Unknown;
	
	/** The jockeys in this race */
	protected Map<String, Jockey> jockeys = new HashMap<String, Jockey>();
	
	/**
	 * Set the course used by this race
	 * @param course The course to use
	 */
	public void setCourse(Racecourse course) {
		this.course = course;
		this.state = RaceState.Waiting;
	}

	/**
	 * Add a jockey to a race
	 * @param player The player who will become the jockey
	 */
	public void addJockey(Player player) {
		
		if (this.state != RaceState.Waiting) {
			MineKart.output(player, "You can't currently join this race.");
			return;
		}
		
		Jockey newJockey = new Jockey(player, this.course.getMountType(), this);
		this.jockeys.put(player.getName(), newJockey);
		
		player.teleport(this.course.getWarp("lobby"));
		MineKart.output(player, "You have joined the lobby for the racecourse '" + this.course.getName() + "'.");		
		
		List<Location> spawns = this.course.getMultiWarp("spawn");
		if (spawns.size() == this.jockeys.size()) {
			teleportToSpawns();
			return;
		}
		
	}

	/**
	 * Teleport all jockeys to the starting spawns and put them on their mounts
	 */
	public void teleportToSpawns() {
		
		this.state = RaceState.Starting;	
		
		List<Location> spawns = this.course.getMultiWarp("spawn");
		int spawnIndex = spawns.size() - 1;
		
		for (Jockey jockey : this.jockeys.values()) {
			Location spawn = spawns.get(spawnIndex);
			jockey.teleportToSpawn(spawn);
			spawnIndex--;
		}
		
		startRace(4);		
	}

	/**
	 * Start the race
	 * @param countdown The amount of time until the race starts
	 */
	protected void startRace(final int countdown) {
		
		if (countdown <= 0) {
			playSoundToRace(Sound.LEVEL_UP, 1.0f, 1.0f);
			onRaceStart();		
			return;
		}
		
		outputToRace("Race starting in " + countdown);
		playSoundToRace(Sound.ORB_PICKUP, 1.0f, 1.0f);
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(MineKart.getInstance(), new Runnable() {

			@Override
			public void run() {
				startRace(countdown - 1);
			}
			
		}, 20L);
		
	}
	
	/**
	 * Play a sound to all jockeys in the race
	 * @param sound The sound to player
	 * @param volume The volume of the sound
	 * @param pitch The pitch of the sound
	 */
	private void playSoundToRace(Sound sound, float volume, float pitch) {
		for (Jockey jockey : this.jockeys.values()) {
			jockey.getPlayer().playSound(jockey.getPlayer().getLocation(), sound, volume, pitch);
		}
	}

	/**
	 * Called when the race starts
	 */
	private void onRaceStart() {
		outputToRace("and they're off!");
		
		this.course.onRaceStart(this);
		
		for (Jockey jockey : this.jockeys.values()) {
			jockey.onRaceStart();
		}
		
		this.state = RaceState.InRace;	
	}

	/**
	 * Output a message to all players in this race
	 * @param message The message to output
	 */
	public void outputToRace(String message) {
		
		for (Jockey jockey : this.jockeys.values()) {
			MineKart.output(jockey.getPlayer(), message);
		}
		
	}
	
	/**
	 * Output a message to all players in this race, from a given player
	 * @param player The player saying the message
	 * @param message The message to output
	 */
	public void outputToRace(Player player, String message) {
		
		for (Jockey jockey : this.jockeys.values()) {
			MineKart.output(jockey.getPlayer(), player, message);
		}
		
	}

	/**
	 * Get the jockey which represents a given player
	 * @param player The player to get the jockey of
	 * @return The jockey, or null if the given player isn't a jockey in this race
	 */
	public Jockey getJockey(Player player) {
		return this.jockeys.get(player.getName());
	}
	
	/**
	 * Remove a jockey from the race
	 * @param jockey The jockey to remove
	 */
	public void removeJockey(Jockey jockey) {
		
		this.jockeys.remove(jockey.getPlayer().getName());
		jockey.onRaceEnd();
		
		if (this.jockeys.isEmpty()) {
			end();
		}
		
	}

	/**
	 * End the race
	 */
	public void end() {
		Map<String, Jockey> tempJockeys = new HashMap<String, Jockey>(this.jockeys);
		for (Jockey jockey : tempJockeys.values()) {
			removeJockey(jockey);
		}
		this.state = RaceState.Waiting;
	}

	/**
	 * Get the current state of the race
	 * @return The state of the race
	 */
	public RaceState getState() {
		return this.state;
	}

	/**
	 * Get the course that this race is using
	 * @return The course instance
	 */
	public Racecourse getCourse() {
		return this.course;
	}
	
	/**
	 * Called when a jockey moves
	 * @param jockey The jockey who moved
	 */
	public void onJockeyMove(Jockey jockey) {
		this.course.onJockeyMove(jockey, this);
	}

	/**
	 * Gets all jockeys in this race
	 * @return A collection of jockeys
	 */
	public Collection<Jockey> getJockeys() {
		return this.jockeys.values();
	}

	/**
	 * Set the winner of the race
	 * @param jockey The jockey who is the winner
	 */
	public void setWinner(Jockey jockey) {
		
		if (this.state != RaceState.InRace)
			return;
		this.state = RaceState.Waiting;
		
		this.outputToRace(
				ChatColor.YELLOW + jockey.getPlayer().getName() + 
				ChatColor.WHITE + " and their mount " + 
				ChatColor.YELLOW + jockey.getMount().getName() +
				ChatColor.WHITE +" are the Winners!"
			);
		
		end();
	}
}
