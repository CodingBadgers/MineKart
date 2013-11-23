package uk.thecodingbadgers.minekart.events.race;

import uk.thecodingbadgers.minekart.events.MineKartEvent;
import uk.thecodingbadgers.minekart.race.Race;

/**
 * Generic type referring to any event that is related to a {@link Race}.
 */
public abstract class RaceEvent extends MineKartEvent {

	protected Race race;

	/**
	 * Instantiates a new race event.
	 * 
	 * @param race the race
	 */
	public RaceEvent(Race race) {
		this.race = race;
	}

	/**
	 * Gets the race that was affected.
	 * 
	 * @return the race that was affected
	 */
	public Race getRace() {
		return race;
	}

}
