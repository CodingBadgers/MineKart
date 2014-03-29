package uk.thecodingbadgers.minekart.powerup;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R2.inventory.CraftItemStack;

import net.minecraft.server.v1_7_R2.DamageSource;
import net.minecraft.server.v1_7_R2.EntityItem;

public class EntityPowerup extends EntityItem {

	public EntityPowerup(Location location, org.bukkit.inventory.ItemStack powerup) {
		super(((CraftWorld) location.getWorld()).getHandle(), location.getX(), location.getBlockY(), location.getZ(), CraftItemStack.asNMSCopy(powerup));
	}

	@Override
	public void move(double d0, double d1, double d2) {

	}

	@Override
	public void h() {
		this.C();
	}

	@Override
	public boolean damageEntity(DamageSource damagesource, float f) {
		return false;
	}

}
