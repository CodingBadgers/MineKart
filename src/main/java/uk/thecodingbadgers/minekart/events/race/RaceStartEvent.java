package uk.thecodingbadgers.minekart.events.race;

import java.util.Set;

import org.bukkit.event.HandlerList;

import com.google.common.collect.ImmutableSet;

import uk.thecodingbadgers.minekart.jockey.Jockey;
import uk.thecodingbadgers.minekart.race.Race;

/**
 * Event call when a race starts, called after the countdown has finished.
 */
public class RaceStartEvent extends RaceEvent {

	private static final HandlerList handlers = new HandlerList();

	public RaceStartEvent(Race race) {
		super(race);
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

}
