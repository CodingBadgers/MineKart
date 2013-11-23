package uk.thecodingbadgers.minekart.events.jockey;

import org.bukkit.event.HandlerList;

import uk.thecodingbadgers.minekart.jockey.Jockey;
import uk.thecodingbadgers.minekart.race.Race;

/**
 * Event call when a jockey joins a race.
 */
public class JockeyJoinEvent extends JockeyEvent {

	private static final HandlerList handlers = new HandlerList();
	private Race race;

	public JockeyJoinEvent(Jockey jockey, Race race) {
		super(jockey);
		this.race = race;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	/**
	 * Gets the race the jockey has joined.
	 * 
	 * @return the race the jockey has joined
	 */
	public Race getRace() {
		return race;
	}

}
