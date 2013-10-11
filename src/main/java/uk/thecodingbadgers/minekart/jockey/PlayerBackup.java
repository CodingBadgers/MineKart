package uk.thecodingbadgers.minekart.jockey;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PlayerBackup implements java.io.Serializable {

	/** Class serial uid */
	private static final long serialVersionUID = -3870744585270256500L;

	/** The players gamemode */
	private GameMode gamemode;
	
	/** The players health */
	private Double health;
	
	/** The players huner level */
	private int hunger;
	
	/** The players experience */
	private float experience;
	
	/** The players inventory's contents */
	private ItemStack[] inventoryContents;
	
	/** The players armour contents */
	private ItemStack[] armourContents;
	
	/**
	 * Backup a given player
	 * @param player The player to backup
	 */
	public void backup(Player player) {
		
		this.gamemode = player.getGameMode();
		this.health = player.getHealth();
		this.hunger = player.getFoodLevel();
		this.experience = player.getExp();
		
		this.inventoryContents = player.getInventory().getContents();
		this.armourContents = player.getInventory().getArmorContents();
		
	}

	/**
	 * Restore a given player
	 * @param player The player to backup
	 */
	public void restore(Player player) {
		
		player.setGameMode(this.gamemode);
		player.setHealth(this.health);
		player.setFoodLevel(this.hunger);
		player.setExp(this.experience);
		
		player.getInventory().setContents(this.inventoryContents);
		player.getInventory().setArmorContents(this.armourContents);
		
	}

}
