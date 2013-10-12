package uk.thecodingbadgers.minekart.listener;

import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.inventory.ItemStack;

import uk.thecodingbadgers.minekart.MineKart;
import uk.thecodingbadgers.minekart.jockey.Jockey;
import uk.thecodingbadgers.minekart.race.Race;
import uk.thecodingbadgers.minekart.race.RaceState;

public class JockeyListener implements Listener {

	/**
	 * Called when a player tried to dismount a vehicle.
	 * @param event The vehicle exit event containing information on this event
	 */
	@EventHandler
	public void onEntityDismount(VehicleExitEvent event) {
		
		LivingEntity jockeyEntity = event.getExited();
		
		if (!(jockeyEntity instanceof Player))
			return;
		
		Jockey jockey = MineKart.getInstance().getJockey((Player)jockeyEntity);
		if (jockey == null)
			return;
		
		event.setCancelled(true);		
	}
	
	/**
	 * Called when a player interacts.
	 * @param event The player interact event containing information on this event
	 */
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		
		Player player = event.getPlayer();
		Jockey jockey = MineKart.getInstance().getJockey(player);
		if (jockey == null)
			return;
		
		if (jockey.getRace().getState() != RaceState.InRace)
			return;
		
		ItemStack item = player.getItemInHand();
		if (item != null && item.getItemMeta() != null && item.getItemMeta().getDisplayName() != null
				&& item.getItemMeta().getDisplayName().equalsIgnoreCase("whip")) {	
			
			int amount = item.getAmount();
			if (amount <= 1) {
				player.getInventory().removeItem(item);
			}
			else {
				item.setAmount(amount - 1);
			}
			player.updateInventory();
			
			jockey.increaseSpeed(2, 4); // strength, time
			jockey.getRace().outputToRace(jockey.getPlayer(), "hYah!");
			jockey.getPlayer().playSound(player.getLocation(), getWhipSound(jockey.getMount().getBukkitEntity().getType()), 2.0f, 1.0f);
		}
		
	}
	
	/**
	 * Get the sound that should be played when the mount is whiped
	 * @param type The mount type
	 * @return The sound to be played
	 */
	private Sound getWhipSound(EntityType type) {
		
		switch(type) {
		case CHICKEN:
			return Sound.CHICKEN_HURT;
		case PIG:
			return Sound.PIG_DEATH;
		case COW:
			return Sound.COW_HURT;
		case HORSE:
			return Sound.HORSE_HIT;
		case SQUID:
			return Sound.SPLASH;
		case BAT:
			return Sound.BAT_HURT;
		case BLAZE:
			return Sound.BLAZE_HIT;
		case CAVE_SPIDER:
			return Sound.SPIDER_DEATH;
		case CREEPER:
			return Sound.EXPLODE;
		case ENDERMAN:
			return Sound.ENDERMAN_HIT;
		case ENDER_DRAGON:
			return Sound.ENDERDRAGON_HIT;
		case GHAST:
			return Sound.GHAST_DEATH;
		case GIANT:
			return Sound.ZOMBIE_HURT;
		case IRON_GOLEM:
			return Sound.IRONGOLEM_HIT;
		case MAGMA_CUBE:
			return Sound.FIRE;
		case MUSHROOM_COW:
			return Sound.COW_HURT;
		case OCELOT:
			return Sound.CAT_HIT;
		case PIG_ZOMBIE:
			return Sound.ZOMBIE_PIG_HURT;
		case SILVERFISH:
			return Sound.SILVERFISH_HIT;
		case SKELETON:
			return Sound.SKELETON_HURT;
		case SPIDER:
			return Sound.SPIDER_DEATH;
		case VILLAGER:
			return Sound.VILLAGER_HIT;
		case WITHER:
			return Sound.WITHER_HURT;
		case WOLF:
			return Sound.WOLF_HURT;
		case ZOMBIE:
			return Sound.ZOMBIE_DEATH;
		default:
			return Sound.HURT;
		}
	}

	/**
	 * Called when a player moves.
	 * @param event The player move event containing information on this event
	 */
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		
		Player player = event.getPlayer();
		Jockey jockey = MineKart.getInstance().getJockey(player);
		if (jockey == null)
			return;
		
		Race race = jockey.getRace();
		if (race.getState() != RaceState.InRace)
			return;

		if (!jockey.hasMoved(player.getLocation()))
			return;
		
		race.onJockeyMove(jockey);
	}
	
	/**
	 * Called when a player is damaged.
	 * @param event The entity damage event containing information on this event
	 */
	@EventHandler
	public void onPlayerMove(EntityDamageEvent event) {
	
		Entity entity = event.getEntity();
		if (!(entity instanceof Player)) {
			event.setCancelled(true);
			return;
		}
		
		Player player = (Player)entity;
		Jockey jockey = MineKart.getInstance().getJockey(player);
		if (jockey == null)
			return;
		
		Race race = jockey.getRace();
		if (race.getState() != RaceState.InRace) {
			event.setCancelled(true);
			return;
		}
		
		if (event.getCause() == DamageCause.ENTITY_ATTACK) {
			event.setCancelled(true);	
			return;
		}
		
		if (event.getDamage() >= player.getHealth()) {
			event.setCancelled(true);
			jockey.respawn();
			return;
		}
		
	}
}
