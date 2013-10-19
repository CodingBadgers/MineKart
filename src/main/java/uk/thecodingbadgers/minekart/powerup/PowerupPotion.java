package uk.thecodingbadgers.minekart.powerup;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import uk.thecodingbadgers.minekart.MineKart;
import uk.thecodingbadgers.minekart.jockey.Jockey;

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
		this.type = PotionEffectType.getByName(file.getString("powerup.potion.type"));
		
	}

	@Override
	public void onPickup(Jockey jockey) {
		
		ItemStack item = new ItemStack(this.material);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(this.name);
		item.setItemMeta(meta);
		
		jockey.getPlayer().getInventory().setItem(1, item);		
		jockey.setPowerup(this);
		
		MineKart.output(jockey.getPlayer(), "You picked up " + this.name);
		
	}

	@Override
	public void onUse(Jockey jockey) {
		
		PotionEffect effect = new PotionEffect(this.type, (int) (this.length * 20), this.level, false);
				
		if (this.applyMode == PowerupApplyMode.Self) {
			jockey.getPlayer().addPotionEffect(effect, true);
			jockey.getMount().getBukkitEntity().addPotionEffect(effect, true);
		}
		
		handleInvisible(jockey);
		
	}

	@SuppressWarnings("deprecation")
	private void handleInvisible(final Jockey jockey) {
		
		if (this.type.getId() != 14) { // Doing it by type doesn't work? but id does
			return;
		}	
		
		PlayerInventory invent = jockey.getPlayer().getInventory();
		invent.setHelmet(null);
		invent.setChestplate(null);
		invent.setLeggings(null);
		invent.setBoots(null);
		jockey.getPlayer().updateInventory();
		
		Bukkit.getScheduler().runTaskLater(MineKart.getInstance(), new Runnable() {

			@Override
			public void run() {
				jockey.equipGear();
			}
			
		}, this.length * 20L);
	}

}
