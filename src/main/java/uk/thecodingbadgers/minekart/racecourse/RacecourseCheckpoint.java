package uk.thecodingbadgers.minekart.racecourse;

import org.bukkit.entity.Player;

/**
 * @author TheCodingBadgers
 *
 * A standard race where jockeys must go through all checkpoints.
 * The first jockey to cross the last checkpoint (after going through all other checkpoints in order)
 * is the winner
 *
 */
public class RacecourseCheckpoint extends Racecourse {

	@Override
	public boolean setup(Player player) {
		
		return false;
	}

}
