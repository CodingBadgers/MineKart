package uk.thecodingbadgers.minekart.events.jockey;

import uk.thecodingbadgers.minekart.events.MineKartEvent;
import uk.thecodingbadgers.minekart.jockey.Jockey;

/**
 * Generic type referring to any event that is caused by a {@link Jockey}.
 */
public abstract class JockeyEvent extends MineKartEvent {

	private Jockey jockey;

	public JockeyEvent(Jockey jockey) {
		this.jockey = jockey;
	}
	
	/**
	 * Gets the {@link Jockey} that caused this event.
	 *
	 * @return the {@link Jockey} that caused the event
	 */
	public Jockey getJockey() {
		return jockey;
	}
}
