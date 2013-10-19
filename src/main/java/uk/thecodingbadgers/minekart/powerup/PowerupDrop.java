package uk.thecodingbadgers.minekart.powerup;

import java.io.File;

import org.bukkit.entity.Player;

public class PowerupDrop extends Powerup {

	public PowerupDrop() {
		// TODO Auto-generated constructor stub
	}
	
	public PowerupDrop(PowerupDrop powerup) {
		super(powerup);
	}
	
	/**
	 * Load the powerup
	 * @param file The file containing the powerup data
	 */
	public void load(File file) {
		super.load(file);
	}

	@Override
	public void onPickup(Player player) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUse(Player player) {
		// TODO Auto-generated method stub

	}

}
