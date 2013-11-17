package uk.thecodingbadgers.minekart.powerup;

import java.io.File;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;

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
	public void onUse(Jockey jockey) {

		this.amount--;

		final Player player = jockey.getPlayer();

		Location spawnLocation = player.getLocation();
		Projectile projectile = (Projectile) player.getWorld().spawnEntity(spawnLocation, this.type);
		projectile.setVelocity(spawnLocation.getDirection().multiply(this.speed));
		projectile.setShooter(player);

	}

}
