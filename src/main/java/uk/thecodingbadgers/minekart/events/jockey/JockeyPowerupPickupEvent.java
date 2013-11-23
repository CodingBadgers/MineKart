package uk.thecodingbadgers.minekart.events.jockey;

import org.bukkit.event.HandlerList;

import uk.thecodingbadgers.minekart.jockey.Jockey;
import uk.thecodingbadgers.minekart.powerup.Powerup;
import uk.thecodingbadgers.minekart.race.Race;

/**
 * Bukkit event called when a jockey picks up a powerup in a race.
 */
public class JockeyPowerupPickupEvent extends JockeyEvent {

	private static final HandlerList handlers = new HandlerList();
	private Race race;
	private Powerup powerup;

	public JockeyPowerupPickupEvent(Jockey jockey, Race race, Powerup powerup) {
		super(jockey);
		this.race = race;
		this.powerup = powerup;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	/**
	 * Gets the race the jockey is in.
	 * 
	 * @return the race
	 */
	public Race getRace() {
		return race;
	}

	/**
	 * Gets the powerup the jockey picked up.
	 * 
	 * @return the powerup
	 */
	public Powerup getPowerup() {
		return powerup;
	}

	/**
	 * Sets the powerup the jockey is going to be given.
	 * 
	 * @param power the new powerup
	 */
	public void setPowerup(Powerup power) {
		this.powerup = power;
	}

}
