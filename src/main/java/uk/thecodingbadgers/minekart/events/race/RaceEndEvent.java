package uk.thecodingbadgers.minekart.events.race;

import org.bukkit.event.HandlerList;

import uk.thecodingbadgers.minekart.jockey.Jockey;
import uk.thecodingbadgers.minekart.race.Race;

/**
 * Event call when a race finishes, either if someone has won, or if everyone
 * leaves.
 */
public class RaceEndEvent extends RaceEvent {

	private static final HandlerList handlers = new HandlerList();

	private Jockey winner;

	public RaceEndEvent(Race race, Jockey jockey) {
		super(race);
		this.winner = jockey;
	}

	/**
	 * Gets the winner of the race.
	 * 
	 * @return the winner of the race
	 */
	public Jockey getWinner() {
		return winner;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

}
