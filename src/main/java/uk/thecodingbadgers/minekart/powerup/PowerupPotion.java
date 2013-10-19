package uk.thecodingbadgers.minekart.powerup;

import java.io.File;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;

import uk.thecodingbadgers.minekart.MineKart;

public class PowerupPotion extends Powerup {
	
	/** The type of potion to apply **/
	private PotionEffectType type;
	
	/** The length to apply the effect **/
	private Long length;
	
	/** The level of potion to use **/
	private int level;

	public PowerupPotion() {
		
	}
	
	public PowerupPotion(PowerupPotion powerup) {
		super(powerup);
		this.type = powerup.type;
		this.length = powerup.length;
		this.level = powerup.level;
	}
	
	/**
	 * Load the powerup
	 * @param file The file containing the powerup data
	 */
	public void load(File configfile) {
		super.load(configfile);
		
		FileConfiguration file = YamlConfiguration.loadConfiguration(configfile);
		
		this.length = file.getLong("powerup.potion.length");
		this.level = file.getInt("powerup.potion.level");
		this.type = PotionEffectType.getByName(file.getString("powerup.potion.length"));
		
	}

	@Override
	public void onPickup(Player player) {
		
		ItemStack item = new ItemStack(this.material);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(this.name);
		item.setItemMeta(meta);
		
		player.getInventory().setItem(1, item);
		
		MineKart.output(player, "You picked up " + this.name);
		
	}

	@Override
	public void onUse(Player player) {
		
	}

}
