package uk.thecodingbadgers.minekart.race;

import org.bukkit.entity.Player;

import uk.thecodingbadgers.minekart.racecourse.Racecourse;

/**
 * @author TheCodingBadgers
 *
 * The interface for races.
 *
 */
public abstract class Race {
	
	protected Racecourse course = null;
	
	/**
	 * Set the course used by this race
	 * @param course The course to use
	 */
	public void setCourse(Racecourse course) {
		this.course = course;
	}

	/**
	 * Add a jockey to a race
	 * @param player
	 */
	public void addJockey(Player player) {
		
		player.teleport(course.getWarp("lobby"));
		
	}

}
