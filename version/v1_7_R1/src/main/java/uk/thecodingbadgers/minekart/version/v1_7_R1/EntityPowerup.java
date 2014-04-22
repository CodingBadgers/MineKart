package uk.thecodingbadgers.minekart.version.v1_7_R1;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R1.inventory.CraftItemStack;

import uk.thecodingbadgers.minekart.powerup.PowerupEntity;

import net.minecraft.server.v1_7_R1.DamageSource;
import net.minecraft.server.v1_7_R1.EntityItem;

public class EntityPowerup extends EntityItem implements PowerupEntity {

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

	@Override
	public void spawn() {
		this.world.addEntity(this);
	}

	@Override
	public void remove() {
		this.die();
	}

}
