package uk.thecodingbadgers.minekart.events.jockey;

import org.bukkit.event.HandlerList;

import uk.thecodingbadgers.minekart.jockey.Jockey;

/**
 * Event call when a jockey leaves a race.
 */
public class JockeyLeaveEvent extends JockeyEvent {

	private static final HandlerList handlers = new HandlerList();

	public JockeyLeaveEvent(Jockey jockey) {
		super(jockey);
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

}
