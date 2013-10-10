package uk.thecodingbadgers.minekart.race;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import uk.thecodingbadgers.minekart.MineKart;
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
		
		Jockey newJockey = new Jockey(player, EntityType.HORSE, this);
		this.jockeys.put(player.getName(), newJockey);
		
		player.teleport(this.course.getWarp("lobby"));
		MineKart.output(player, "You have joined the lobby for the racecourse '" + this.course.getName() + "'.");		
		
		List<Location> spawns = this.course.getMultiWarp("spawn");
		if (spawns.size() == this.jockeys.size()) {
			this.state = RaceState.InRace;
			teleportToSpawns();
			return;
		}
		
	}

	/**
	 * Teleport all jockeys to the starting spawns and put them on their mounts
	 */
	public void teleportToSpawns() {
		
		List<Location> spawns = this.course.getMultiWarp("spawn");
		int spawnIndex = spawns.size() - 1;
		
		for (Jockey jockey : this.jockeys.values()) {
			Location spawn = spawns.get(spawnIndex);
			jockey.teleportToSpawn(spawn);
			spawnIndex--;
		}
		
		startRace(5);		
	}

	/**
	 * Start the race
	 * @param countdown The amount of time until the race starts
	 */
	protected void startRace(final int countdown) {
		
		if (countdown <= 0) {
			outputToRace("and their off!");
			
			for (Jockey jockey : this.jockeys.values()) {
				jockey.onRaceStart();
			}
						
			return;
		}
		
		outputToRace("Race starting in " + countdown);
		Bukkit.getScheduler().scheduleSyncDelayedTask(MineKart.getInstance(), new Runnable() {

			@Override
			public void run() {
				startRace(countdown - 1);
			}
			
		}, 20L);
		
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
		
	}

	/**
	 * End the race
	 */
	public void end() {
		Map<String, Jockey> tempJockeys = this.jockeys;
		for (Jockey jockey : tempJockeys.values()) {
			removeJockey(jockey);
		}
	}
}
