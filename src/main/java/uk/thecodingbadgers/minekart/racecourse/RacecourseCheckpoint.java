package uk.thecodingbadgers.minekart.racecourse;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import uk.thecodingbadgers.minekart.MineKart;

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
	 * Setup the racecourse.
	 * @param player The player who is setting up the course
	 * @return True if the location is within the course bounds, false otherwise.
	 */
	@Override
	public boolean setup(Player player, String name) {
		
		if (!super.setup(player, name))
			return false;
		
		this.checkPoints = new ArrayList<Region>();
		
		return true;
	}
	
	/**
	 * Load the racecourse from file.
	 */	
	@Override
	public void load(FileConfiguration file) {
		
		super.load(file);
		
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
	public void save(FileConfiguration file) {
		
		super.save(file);
		
		// Checkpoints
		file.set("racecourse.checkpoint.count", this.checkPoints.size());
		int checkpointIndex = 0;
		for (Region checkpoint : this.checkPoints) {
			saveRegion(file, "racecourse.checkpoint." + checkpointIndex, checkpoint);
			checkpointIndex++;
		}
		
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

}
