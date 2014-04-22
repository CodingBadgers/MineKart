package uk.thecodingbadgers.minekart.version;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import uk.thecodingbadgers.minekart.jockey.Mount;
import uk.thecodingbadgers.minekart.powerup.PowerupEntity;

public interface Version {

	public Class<? extends PowerupEntity> getPowerupClass();
	
	public Class<? extends Mount> getMountClass();
	
	public PowerupEntity newPowerup(Location loc, ItemStack stack);
	
	public Mount newMount(boolean b);
	
}
