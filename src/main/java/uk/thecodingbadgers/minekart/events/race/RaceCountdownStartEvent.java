package uk.thecodingbadgers.minekart.events.race;

import java.util.Set;

import org.bukkit.event.HandlerList;

import uk.thecodingbadgers.minekart.jockey.Jockey;
import uk.thecodingbadgers.minekart.race.Race;

import com.google.common.collect.ImmutableSet;

/**
 * Event call when a race starts its countdown.
 */
public class RaceCountdownStartEvent extends RaceEvent {

	private static final HandlerList handlers = new HandlerList();
	private int length;

	public RaceCountdownStartEvent(Race race, int length) {
		super(race);
		this.length = length;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	/**
	 * Gets the jockeys that are in the race.
	 *
	 * @return a immutable set of the jockeys in the race
	 */
	public Set<Jockey> getJockeys() {
		return ImmutableSet.copyOf(race.getJockeys());
	}
	
	/**
	 * Gets the coundown length.
	 *
	 * @return the coundown length for the race
	 */
	public int getCoundownLength() {
		return length;
	}
	
	/**
	 * Sets the countdown length.
	 *
	 * @param length the new countdown length for the race
	 */
	public void setCountdownLength(int length) {
		this.length = length;
	}

}
