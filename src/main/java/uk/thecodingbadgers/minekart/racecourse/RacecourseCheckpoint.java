package uk.thecodingbadgers.minekart.racecourse;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import uk.thecodingbadgers.minekart.MineKart;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;
import com.sk89q.worldedit.regions.Region;

/**
 * @author TheCodingBadgers
 *
 * A standard race where jockeys must go through all checkpoints.
 * The first jockey to cross the last checkpoint (after going through all other checkpoints in order)
 * is the winner
 *
 */
public class RacecourseCheckpoint extends Racecourse {
	
	/** The checkpoints a jockey must pass through to complete the race */
	protected List<Region> checkPoints = null;
	
	/**
	 * Class constructor
	 */
	public RacecourseCheckpoint() {
		this.type = "checkpoint";
	}

	/**
	 * Setup the racecourse.
	 * @param player The player who is setting up the course
	 * @return True if the location is within the course bounds, false otherwise.
	 */
	@Override
	public boolean setup(Player player, String name) {
				
		if (!super.setup(player, name))
			return false;
		
		this.checkPoints = new ArrayList<Region>();
		
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
		} catch (Exception ex) {}
		
	}
	
	/**
	 * Output the remaining requirements to complete this arena
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
		
		if (fullySetup) {
			MineKart.output(sender, "The course '" + this.name + "' is fully setup!");
		}
		
		return fullySetup;
	}
	
	/**
	 * Add a multi-point warp
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
				this.checkPoints.add(selection.getRegionSelector().getRegion());
			} catch(Exception ex) {
				MineKart.output(player, "An invalid selection was made using world edit. Please make a complete cuboid selection and try again.");
				return;
			}
			
			MineKart.output(player, "Succesfully add a new checkpoint to the arena!");
			save();
			return;
		}
		
		// if it's not a checkpoint pass it on
		super.addWarp(player, warpname);
		save();
		
	}

}
