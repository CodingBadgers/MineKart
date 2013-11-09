package uk.thecodingbadgers.minekart.powerup;

import java.io.File;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import uk.thecodingbadgers.minekart.MineKart;
import uk.thecodingbadgers.minekart.jockey.Jockey;

public abstract class Powerup {

	/** The slot a powerup is put in */
	public static final int POWERUP_SLOT = 1;

	/** The name of the powerup **/
	protected String name;

	/** The powerup mode **/
	protected PowerupUseMode useMode;

	/** The material the powerup is displayed as **/
	protected Material material;

	/** The number of uses **/
	protected int amount;

	/**
	 * Class constructor
	 */
	public Powerup() {

	}

	/**
	 * Copy constructor
	 * 
	 * @param powerup The powerup to copy from
	 */
	public Powerup(Powerup powerup) {
		this.name = powerup.name;
		this.useMode = powerup.useMode;
		this.material = powerup.material;
		this.amount = powerup.amount;
	}

	/**
	 * Load the powerup
	 * 
	 * @param configfile The file containing the powerup data
	 */
	public void load(File configfile) {

		FileConfiguration file = YamlConfiguration.loadConfiguration(configfile);

		this.name = file.getString("powerup.name");
		this.material = Material.valueOf(file.getString("powerup.material"));
		this.amount = file.getInt("powerup.amount");

	}

	/**
	 * Called when the powerup is picked up
	 * 
	 * @param jockey The player who picked it up
	 */
	public void onPickup(Jockey jockey) {

		ItemStack item = new ItemStack(this.material, this.amount);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(this.name);
		item.setItemMeta(meta);

		jockey.getPlayer().getInventory().setItem(POWERUP_SLOT, item);
		jockey.setPowerup(this);

		MineKart.output(jockey.getPlayer(), "You picked up " + this.name);
	}

	/**
	 * Called when the powerup is used
	 * 
	 * @param jockey The player who used it
	 */
	public abstract void onUse(Jockey jockey);

	/**
	 * Called when the powerup is dropped
	 * 
	 * @param player The player who picked it up
	 */
	public void onDrop(Player player) {
		player.getInventory().setItem(POWERUP_SLOT, new ItemStack(Material.AIR));
	}

	/**
	 * Gets the use mode of the potion
	 * 
	 * @return The use mode
	 */
	public PowerupUseMode getUseMode() {
		return useMode;
	}

	/**
	 * Gets the item of the potion
	 * 
	 * @return The itemstack
	 */
	public Material getMaterial() {
		return material;
	}

	/**
	 * Gets the amount of uses a powerup has
	 * 
	 * @return The amount of uses
	 */
	public int getAmount() {
		return this.amount;
	}
	
	/**
	 * Gets the name of the powerup
	 * 
	 * @return The name of the powerup
	 */
	public String getName() {
		return this.name;
	}

}
