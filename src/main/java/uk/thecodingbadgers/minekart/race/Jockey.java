package uk.thecodingbadgers.minekart.race;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.trait.Controllable;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * @author TheCodingBadgers
 *
 * A jockey consists of a player and his mount. A mount is any entity that can be controlled.
 * Jockeys also hold any powerups attained during a race. 
 *
 */
public class Jockey {
	
	/** The player which represents this jockey */
	private Player player = null;
	
	/** The type of mount the jockey will use */
	private EntityType mountType = EntityType.UNKNOWN;
	
	/** The jockeys mount */
	private NPC mount = null;

	/**
	 * 
	 * @param player
	 * @param mountType
	 */
	public Jockey(Player player, EntityType mountType) {
		this.player = player;
		this.mountType = mountType;
	}

	/**
	 * Get the player which represents this jockey
	 * @return The player
	 */
	public Player getPlayer() {
		return this.player;
	}

	/**
	 * Teleport a jockey to a spawn and put them on their mount 
	 * @param spawn The spawn location
	 */
	public void teleportToSpawn(Location spawn) {
		
		// make their mounts
		this.mount = CitizensAPI.getNPCRegistry().createNPC(this.mountType, "Horse");
		this.mount.setProtected(true);
		this.mount.addTrait(new Controllable(false));
		this.mount.spawn(spawn);
		
		// Make the npc controllable and mount the player
		Controllable trait = this.mount.getTrait(Controllable.class);
		trait.setEnabled(true);
		trait.mount(this.player);	
		trait.setEnabled(false);
		
		// Give the player a whip
		ItemStack whip = new ItemStack(Material.STICK);
		ItemMeta meta = whip.getItemMeta();
		meta.setDisplayName("Whip");
		whip.setItemMeta(meta);
		player.getInventory().setItem(0, whip);
	}
	
	/**
	 * Called when a race starts
	 */
	public void onRaceStart() {
		
		Controllable trait = this.mount.getTrait(Controllable.class);
		trait.setEnabled(true);
		
	}

	/**
	 * Call when a race has ended
	 */
	public void onRaceEnd() {
		this.mount.destroy();
	}

}
