package uk.thecodingbadgers.minekart.powerup;

import java.io.File;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import uk.thecodingbadgers.minekart.jockey.Jockey;

public abstract class Powerup {
	
	/** The name of the powerup **/
	protected String name;
	
	/** The powerup mode **/
	protected PowerupApplyMode applyMode;
	
	/** The powerup mode **/
	protected PowerupUseMode useMode;
	
	/** The material the powerup is displayed as */
	protected Material material;
	
	/**
	 * Class constructor
	 */
	public Powerup() {
		
	}
	
	/**
	 * Copy constructor
	 * @param powerup The powerup to copy from
	 */
	public Powerup(Powerup powerup) {
		this.name = powerup.name;
		this.applyMode = powerup.applyMode;
		this.useMode = powerup.useMode;
		this.material = powerup.material;
	}
	
	/**
	 * Called when the powerup is picked up
	 * @param player The player who picked it up
	 */
	public abstract void onPickup(Jockey jockey);
	
	/**
	 * Called when the powerup is used
	 * @param player The player who used it
	 */
	public abstract void onUse(Jockey jockey);
	
	/**
	 * Called when the powerup is dropped
	 * @param player The player who picked it up
	 */
	public void onDrop(Player player) {
		player.getInventory().setItem(1, new ItemStack(Material.AIR));
	}

	/**
	 * Gets the apply mode of the potion
	 * @return The apply mode
	 */
	public PowerupApplyMode getApplyMode() {
		return applyMode;
	}
	
	/**
	 * Gets the use mode of the potion
	 * @return The use mode
	 */
	public PowerupUseMode getUseMode() {
		return useMode;
	}
	
	/**
	 * Gets the item of the potion
	 * @return The itemstack
	 */
	public Material getMaterial() {
		return material;
	}

	/**
	 * Load the powerup
	 * @param file The file containing the powerup data
	 */
	public void load(File configfile) {
		
		FileConfiguration file = YamlConfiguration.loadConfiguration(configfile);
		
		this.name = file.getString("powerup.name");
		this.material = Material.valueOf(file.getString("powerup.material"));
		this.applyMode = PowerupApplyMode.valueOf(file.getString("powerup.mode.apply"));
		this.useMode = PowerupUseMode.valueOf(file.getString("powerup.mode.use"));
		
	}

}
