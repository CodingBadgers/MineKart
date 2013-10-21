package uk.thecodingbadgers.minekart.powerup;

import java.io.File;

import uk.thecodingbadgers.minekart.jockey.Jockey;

public class PowerupDrop extends Powerup {

	public PowerupDrop() {
		this.useMode = PowerupUseMode.Drop;
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
	public void onUse(Jockey jockey) {
		
	}

}
