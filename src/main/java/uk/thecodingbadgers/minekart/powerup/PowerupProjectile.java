package uk.thecodingbadgers.minekart.powerup;

import java.io.File;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.util.Vector;

import uk.thecodingbadgers.minekart.jockey.Jockey;

public class PowerupProjectile extends Powerup {

	/** Type of projectile to launch **/
	private EntityType type;

	/** The speed of the projectile **/
	private double speed;

	/**
	 * Class constructor
	 */
	public PowerupProjectile() {
		this.useMode = PowerupUseMode.Projectile;
	}

	/**
	 * Copy constructor
	 * 
	 * @param powerup Powerup to copy from
	 */
	public PowerupProjectile(PowerupProjectile powerup) {
		super(powerup);
		this.speed = powerup.speed;
		this.type = powerup.type;
	}

	/**
	 * Load the powerup
	 * 
	 * @param configfile The file containing the powerup data
	 */
	public void load(File configfile) {
		super.load(configfile);

		FileConfiguration file = YamlConfiguration.loadConfiguration(configfile);

		this.speed = file.getDouble("powerup.projectile.speed");
		this.type = EntityType.valueOf(file.getString("powerup.projectile.type"));

	}

	/**
	 * Called when the powerup is used
	 * 
	 * @param jockey The player who used it
	 */
	@Override
	@SuppressWarnings("deprecation")
	public void onUse(Jockey jockey) {

		this.amount--;

		final Player player = jockey.getPlayer();

		Location spawnLocation = player.getLocation();
		Vector mountDirection = jockey.getMount().getBukkitEntity().getLocation().getDirection();

		float scaler = this.speed < 0 ? -2.0f : 2.0f;
		spawnLocation = spawnLocation.add(new Vector(scaler * mountDirection.getX(), 0.1, scaler * mountDirection.getZ()));

		Projectile projectile = (Projectile) player.getWorld().spawnEntity(spawnLocation, this.type);
		projectile.setVelocity(mountDirection.multiply(this.speed));
		projectile.setShooter(player);

	}

}
