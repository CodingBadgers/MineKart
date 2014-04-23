package uk.thecodingbadgers.minekart.racecourse;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.BlockState;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import uk.thecodingbadgers.minekart.MineKart;
import uk.thecodingbadgers.minekart.events.jockey.JockeyCheckpointReachedEvent;
import uk.thecodingbadgers.minekart.jockey.Jockey;
import uk.thecodingbadgers.minekart.race.Race;
import uk.thecodingbadgers.minekart.race.RaceState;
import uk.thecodingbadgers.minekart.world.BlockChangeDelagator;
import uk.thecodingbadgers.minekart.world.BlockDelagatorFactory;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;
import com.sk89q.worldedit.regions.Region;

/**
 * @author TheCodingBadgers
 * 
 *         A standard race where jockeys must go through all checkpoints. The
 *         first jockey to cross the last checkpoint (after going through all
 *         other checkpoints in order) is the winner
 * 
 */
public class RacecourseCheckpoint extends Racecourse {

	class CheckpointTimeData {
		public CheckpointTimeData(int checkpoint, long time) {
			this.checkpoint = checkpoint;
			this.time = time;
		}

		public int checkpoint;
		public long time;
	}

	/** The checkpoints a jockey must pass through to complete the race */
	protected List<Region> checkPoints = null;

	/** A map of a jockey and their next checkpoint id */
	protected Map<Jockey, Integer> targetCheckpoints = null;

	/** A map of a jockey and their checkpoint time data */
	protected Map<Jockey, CheckpointTimeData> checkpointTime = null;

	/**
	 * Class constructor
	 */
	public RacecourseCheckpoint() {
        this.pointMappings.put("checkpoint", new ItemStack[] {new ItemStack(Material.WOOL), new ItemStack(Material.WOOL, 0, (short) 15) });
        
		this.type = "checkpoint";
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
		this.checkpointTime = new HashMap<Jockey, CheckpointTimeData>();

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

		// Checkpoints
		this.checkPoints = new ArrayList<Region>();
		this.targetCheckpoints = new HashMap<Jockey, Integer>();
		this.checkpointTime = new HashMap<Jockey, CheckpointTimeData>();

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
	@SuppressWarnings("deprecation")
	@Override
	public boolean onJockeyMove(Jockey jockey, Race race) {

		if (!super.onJockeyMove(jockey, race))
			return false;

		if (race.getState() != RaceState.InRace)
			return false;

		// player has finished the race
		if (!this.targetCheckpoints.containsKey(jockey))
			return true;

		final int targetCheckpointIndex = this.targetCheckpoints.get(jockey);
		Region targetCheckpoint = this.checkPoints.get(targetCheckpointIndex);

		com.sk89q.worldedit.Vector position = jockey.getWorldEditLocation();

		if (targetCheckpoint.contains(position)) {

			//
			CheckpointTimeData data = this.checkpointTime.get(jockey);
			data.checkpoint = targetCheckpointIndex;
			data.time = jockey.getRaceTime();
			updateRankings(race);

			int standing = 1;
			List<Jockey> rankings = race.getRankings();
			for (Jockey j : rankings) {
				race.getScoreboardManager().setJockyStanding(j, standing);
				standing++;
			}

			//
			Location respawnLocation = jockey.getPlayer().getLocation();
			if (jockey.getMount() != null) {
				respawnLocation = jockey.getMount().getBukkitEntity().getLocation();
			}
			jockey.updateRespawnLocation(respawnLocation);

			JockeyCheckpointReachedEvent event = new JockeyCheckpointReachedEvent(jockey, race, targetCheckpointIndex);
			Bukkit.getPluginManager().callEvent(event);

			if (targetCheckpointIndex < this.checkPoints.size() - 1) {

				Player player = jockey.getPlayer();
				Location location = player.getLocation();

				this.targetCheckpoints.put(jockey, targetCheckpointIndex + 1);
				MineKart.output(jockey.getPlayer(), "Checkpoint [" + (targetCheckpointIndex + 1) + "/" + this.checkPoints.size() + "]    " + ChatColor.GREEN + MineKart.formatTime(data.time));

				player.playSound(location, Sound.ORB_PICKUP, 1.0f, 1.0f);

				float lapComplete = 1.0f / ((float) this.checkPoints.size() / (float) (targetCheckpointIndex + 1.0f));
				player.setExp(lapComplete);

			} else {
				MineKart.output(jockey.getPlayer(), "Checkpoint [" + (targetCheckpointIndex + 1) + "/" + this.checkPoints.size() + "]    " + ChatColor.GREEN + MineKart.formatTime(data.time));
				this.targetCheckpoints.remove(jockey);
				race.setWinner(jockey);
			}
		}

		return true;
	}

	/**
	 * Update the rankings of the race, based upon checkpoint/time progress
	 * 
	 * @param race The race in progress
	 */
	private void updateRankings(Race race) {

		List<Jockey> ranks = new ArrayList<Jockey>();

		for (int checkpointIndex = this.checkPoints.size() - 1; checkpointIndex >= 0; --checkpointIndex) {

			// Get all jockeys at this checkpoint
			Map<Jockey, CheckpointTimeData> checkpointRanks = new HashMap<Jockey, CheckpointTimeData>();
			for (Jockey jockey : race.getJockeys()) {
				CheckpointTimeData data = this.checkpointTime.get(jockey);
				if (data.checkpoint == checkpointIndex) {
					checkpointRanks.put(jockey, data);
				}
			}

			// Sort the data by time
			while (!checkpointRanks.isEmpty()) {

				Entry<Jockey, CheckpointTimeData> fastest = null;
				for (Entry<Jockey, CheckpointTimeData> jockeyData : checkpointRanks.entrySet()) {
					if (fastest == null) {
						fastest = jockeyData;
						continue;
					}

					if (jockeyData.getValue().time < fastest.getValue().time) {
						fastest = jockeyData;
					}
				}

				ranks.add(fastest.getKey());
				checkpointRanks.remove(fastest.getKey());
			}
		}

		race.setRankings(ranks);

	}

	/**
	 * Called on race start
	 * 
	 * @param race The race which started
	 */
	@Override
	public void onRaceStart(Race race) {
		super.onRaceStart(race);
		this.targetCheckpoints.clear();
		for (Jockey jockey : race.getJockeys()) {
			this.targetCheckpoints.put(jockey, 0);
			this.checkpointTime.put(jockey, new CheckpointTimeData(-1, -1));
			jockey.getPlayer().setLevel(0);
		}
	}

	@Override
	public boolean showWarps(final Player player, String warptype) {
		return showWarps(BlockDelagatorFactory.createChangeDelagator("fake", player), warptype);
	}

	@Override
	protected boolean showWarps(BlockChangeDelagator delagator, String warptype) {
		if (super.showWarps(delagator, warptype)) {
			return true;
		}

		final Map<Location, BlockState> changes = new HashMap<Location, BlockState>();

		if (warptype.startsWith("checkpoint")) {
			int blockIndex = 0;
			ItemStack[] materials = this.pointMappings.get("checkpoint");

			for (Region checkpoint : this.checkPoints) {
				for (BlockVector block : checkpoint) {
					ItemStack mat = materials[blockIndex];
					Location loc = toBukkit(block);

					if (loc.getBlock().getType() != Material.AIR) {
						continue;
					}

					changes.put(loc, loc.getBlock().getState());

					delagator.setBlock(loc, mat.getType(), mat.getData());

					blockIndex++;

					if (blockIndex >= materials.length) {
						blockIndex = 0;
					}
				}
			}

		} else {
			return false;
		}

		delagator.delayResetChanges(5 * 20L);

		return true;
	}

}
