package uk.thecodingbadgers.minekart.racecourse;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import uk.thecodingbadgers.minekart.MineKart;
import uk.thecodingbadgers.minekart.jockey.Jockey;
import uk.thecodingbadgers.minekart.race.Race;
import uk.thecodingbadgers.minekart.race.RaceState;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;
import com.sk89q.worldedit.regions.Region;

/**
 * @author TheCodingBadgers
 * 
 *         A standard race where jockeys must complete a given number of laps
 *         of a course. There are a given number of checkpoints. A jockey must
 *         go through all checkpoints in the correct order, once a jockey has
 *         passed through the last checkpoint the given number of laps they
 *         are the winner
 * 
 */
public class RacecourseLap extends Racecourse {

	/** The number of laps in the race */
	private int noofLaps = 3;

	/** The current lap */
	protected Map<Jockey, Integer> currentLap = null;

	/** A map of a jockey and their next checkpoint id */
	protected Map<Jockey, Integer> targetCheckpoints = null;

	/** The checkpoints a jockey must pass through to complete the race */
	protected List<Region> checkPoints = null;

	/**
	 * Class constructor
	 */
	public RacecourseLap() {
		this.type = "lap";
	}

	/**
	 * Setup the racecourse.
	 * 
	 * @param player The player who is setting up the course
	 * @return True if the location is within the course bounds, false
	 *         otherwise.
	 */
	@Override
	public boolean setup(Player player, String name) {

		if (!super.setup(player, name))
			return false;

		this.checkPoints = new ArrayList<Region>();
		this.targetCheckpoints = new HashMap<Jockey, Integer>();
		this.currentLap = new HashMap<Jockey, Integer>();

		save();
		return true;
	}

	/**
	 * Load the racecourse from file.
	 */
	@Override
	public void load(File configfile) {

		super.load(configfile);

		FileConfiguration file = YamlConfiguration.loadConfiguration(configfile);

		// Number of laps
		this.noofLaps = file.getInt("racecourse.noofLaps");

		// Checkpoints
		this.checkPoints = new ArrayList<Region>();
		this.targetCheckpoints = new HashMap<Jockey, Integer>();
		this.currentLap = new HashMap<Jockey, Integer>();

		int noofCheckpoints = file.getInt("racecourse.checkpoint.count");
		for (int checkpointIndex = 0; checkpointIndex < noofCheckpoints; ++checkpointIndex) {
			this.checkPoints.add(loadRegion(file, "racecourse.checkpoint." + checkpointIndex));
		}

	}

	/**
	 * Save the racecourse to file.
	 */
	@Override
	public void save() {

		super.save();

		FileConfiguration file = YamlConfiguration.loadConfiguration(this.fileConfiguration);

		// Number of laps
		file.set("racecourse.noofLaps", this.noofLaps);

		// Checkpoints
		file.set("racecourse.checkpoint.count", this.checkPoints.size());
		int checkpointIndex = 0;
		for (Region checkpoint : this.checkPoints) {
			saveRegion(file, "racecourse.checkpoint." + checkpointIndex, checkpoint);
			checkpointIndex++;
		}

		try {
			file.save(this.fileConfiguration);
		} catch (Exception ex) {
		}

	}

	/**
	 * Output the remaining requirements to complete this arena
	 * 
	 * @param sender The sender to receive the output information
	 * @return True if all requirements have been met
	 */
	@Override
	public boolean outputRequirements(CommandSender sender) {

		boolean fullySetup = super.outputRequirements(sender);

		if (this.checkPoints.isEmpty()) {
			MineKart.output(sender, " - Add checkpoints (minimum of 1 required) [/mk addcheckpoint <coursename>]");
			fullySetup = false;
		}

		return fullySetup;
	}

	/**
	 * Output all information about this racecourse
	 * 
	 * @param sender The thing to tell the information
	 */
	@Override
	public void outputInformation(CommandSender sender) {

		super.outputInformation(sender);

		MineKart.output(sender, "Checkpoints:");
		int checkpointIndex = 0;
		for (Region point : this.checkPoints) {
			MineKart.output(sender, "[" + checkpointIndex + "] " + point.toString());
			checkpointIndex++;
		}
		MineKart.output(sender, "-------------");

	}

	/**
	 * Get the number of laps required to complete this racecourse.
	 * 
	 * @return The number of laps in this racecourse
	 */
	public int getNumberOfLaps() {
		return this.noofLaps;
	}

	/**
	 * Add a multi-point warp
	 * 
	 * @param player The player adding the warp
	 * @param warpname The name of the warp to add to
	 */
	@Override
	public void addWarp(Player player, String warpname) {

		if (warpname.equalsIgnoreCase("checkpoint")) {

			WorldEditPlugin worldEdit = MineKart.getInstance().getWorldEditPlugin();
			Selection selection = worldEdit.getSelection(player);
			if (selection == null) {
				MineKart.output(player, "Please make a world edit selection of the region you wish to be a checkpoint...");
				return;
			}

			try {
				this.checkPoints.add(selection.getRegionSelector().getRegion().clone());
			} catch (Exception ex) {
				MineKart.output(player, "An invalid selection was made using world edit. Please make a complete cuboid selection and try again.");
				return;
			}

			MineKart.output(player, "Succesfully add a new checkpoint to the arena!");
			outputRequirements(player);
			save();
			return;
		}

		// if it's not a checkpoint pass it on
		super.addWarp(player, warpname);
		outputRequirements(player);
		save();

	}

	/**
	 * Called when a jockey moves
	 * 
	 * @param jockey The jockey who moved
	 * @param race The race the jockeys are in
	 */
	@Override
	public boolean onJockeyMove(Jockey jockey, Race race) {

		if (!super.onJockeyMove(jockey, race))
			return false;

		if (race.getState() != RaceState.InRace)
			return false;

		int targetCheckpointIndex = this.targetCheckpoints.get(jockey);
		Region targetCheckpoint = this.checkPoints.get(targetCheckpointIndex);

		Location location = jockey.getPlayer().getLocation();
		com.sk89q.worldedit.Vector position = new com.sk89q.worldedit.Vector(location.getBlockX(), location.getBlockY(), location.getBlockZ());

		if (targetCheckpoint.contains(position)) {
			jockey.updateRespawnLocation(jockey.getMount().getBukkitEntity().getLocation());

			if (targetCheckpointIndex < this.checkPoints.size() - 1) {
				this.targetCheckpoints.remove(jockey);
				this.targetCheckpoints.put(jockey, targetCheckpointIndex + 1);
				MineKart.output(jockey.getPlayer(), "Checkpoint [" + (targetCheckpointIndex + 1) + "/" + this.checkPoints.size() + "]      " + ChatColor.GREEN + MineKart.formatTime(jockey.getRaceTime()));

				jockey.getPlayer().playSound(location, Sound.ORB_PICKUP, 1.0f, 1.0f);

				float lapComplete = 1.0f / ((float) this.checkPoints.size() / (float) (targetCheckpointIndex + 1.0f));
				jockey.getPlayer().setExp(lapComplete);
			} else {
				int jockeyLap = this.currentLap.get(jockey) + 1;
				this.currentLap.remove(jockey);
				this.currentLap.put(jockey, jockeyLap);

				MineKart.output(jockey.getPlayer(), "Lap Complete [" + jockeyLap + "/" + this.noofLaps + "]   " + ChatColor.GREEN + MineKart.formatTime(jockey.getRaceTime()));

				jockey.getPlayer().setLevel(jockeyLap);
				jockey.getPlayer().setExp(0.0f);
				jockey.getPlayer().playSound(location, Sound.LEVEL_UP, 1.0f, 1.0f);

				if (jockeyLap >= this.noofLaps) {
					race.setWinner(jockey);
				} else {
					this.targetCheckpoints.remove(jockey);
					this.targetCheckpoints.put(jockey, 0);
				}
			}
		}

		return true;

	}

	/**
	 * Called on race start
	 * 
	 * @param race The race which started
	 */
	@Override
	public void onRaceStart(Race race) {
		super.onRaceStart(race);
		this.currentLap.clear();
		this.targetCheckpoints.clear();
		for (Jockey jockey : race.getJockeys()) {
			this.targetCheckpoints.put(jockey, 0);
			this.currentLap.put(jockey, 0);
			jockey.getPlayer().setLevel(0);
		}
	}

}
