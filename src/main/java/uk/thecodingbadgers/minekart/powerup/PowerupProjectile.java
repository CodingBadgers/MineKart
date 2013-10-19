package uk.thecodingbadgers.minekart.powerup;

import java.io.File;

import uk.thecodingbadgers.minekart.jockey.Jockey;

public class PowerupProjectile extends Powerup {

	public PowerupProjectile() {
		// TODO Auto-generated constructor stub
	}
	
	public PowerupProjectile(PowerupProjectile powerup) {
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
	public void onPickup(Jockey jockey) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUse(Jockey jockey) {
		// TODO Auto-generated method stub

	}

}
