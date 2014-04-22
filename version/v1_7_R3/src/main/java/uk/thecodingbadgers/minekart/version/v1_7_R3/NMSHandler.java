package uk.thecodingbadgers.minekart.version.v1_7_R3;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import uk.thecodingbadgers.minekart.jockey.Mount;
import uk.thecodingbadgers.minekart.powerup.PowerupEntity;
import uk.thecodingbadgers.minekart.version.Version;

public class NMSHandler implements Version {

	@Override
	public Class<? extends PowerupEntity> getPowerupClass() {
		return EntityPowerup.class;
	}

	@Override
	public Class<? extends Mount> getMountClass() {
		return ControllableMount.class;
	}

	@Override
	public PowerupEntity newPowerup(Location loc, ItemStack stack) {
		return new EntityPowerup(loc, stack);
	}

	@Override
	public Mount newMount(boolean b) {
		return new ControllableMount(b);
	}
}
