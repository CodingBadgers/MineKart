package uk.thecodingbadgers.minekart.racecourse;

import org.bukkit.entity.Player;

/**
 * @author TheCodingBadgers
 *
 * A standard race where jockeys must complete a given number of laps of a course.
 * There are a given number of checkpoints. A jockey must go through all
 * checkpoints in the correct order, once a jockey has passed through the
 * last checkpoint the given number of laps they are the winner
 *
 */
public class RacecourseLap extends Racecourse {

	@Override
	public boolean setup(Player player) {
		
		return false;
	}

}
