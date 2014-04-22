package uk.thecodingbadgers.minekart.events.jockey;

import org.bukkit.event.HandlerList;

import uk.thecodingbadgers.minekart.jockey.Jockey;
import uk.thecodingbadgers.minekart.race.Race;

/**
 * Event call when a jockey reaches a checkpoint.
 */
public class JockeyCheckpointReachedEvent extends JockeyEvent {

	private static final HandlerList handlers = new HandlerList();
	private Race race;
	private int checkpoint;

	public JockeyCheckpointReachedEvent(Jockey jockey, Race race, int checkpoint) {
		super(jockey);
		this.race = race;
		this.checkpoint = checkpoint;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	/**
	 * Gets the race the jockey was in.
	 * 
	 * @return the race
	 */
	public Race getRace() {
		return race;
	}

	/**
	 * Gets the checkpoint index the jockey reached, not this is zero indexed.
	 * 
	 * @return the checkpoint
	 */
	public int getCheckpoint() {
		return checkpoint;
	}

}
