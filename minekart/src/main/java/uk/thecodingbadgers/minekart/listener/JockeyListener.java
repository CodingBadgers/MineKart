package uk.thecodingbadgers.minekart.listener;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.citizensnpcs.api.npc.NPC;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.potion.PotionEffectType;

import uk.thecodingbadgers.minekart.MineKart;
import uk.thecodingbadgers.minekart.events.jockey.JockeyPowerupPickupEvent;
import uk.thecodingbadgers.minekart.jockey.Jockey;
import uk.thecodingbadgers.minekart.jockey.Mount;
import uk.thecodingbadgers.minekart.lang.LangUtils;
import uk.thecodingbadgers.minekart.lobby.LobbySign;
import uk.thecodingbadgers.minekart.lobby.LobbySignManager;
import uk.thecodingbadgers.minekart.mount.MountType;
import uk.thecodingbadgers.minekart.powerup.Powerup;
import uk.thecodingbadgers.minekart.race.Race;
import uk.thecodingbadgers.minekart.race.RaceState;
import uk.thecodingbadgers.minekart.version.NmsHandler;

public class JockeyListener implements Listener {

	/**
	 * Handle jockeys quiting from the game
	 * 
	 * @param event The player kicked event
	 */
	@EventHandler
	public void onJockeyLeave(PlayerQuitEvent event) {

		Player player = event.getPlayer();
		Jockey jockey = MineKart.getInstance().getJockey(player);

		if (jockey == null) {
			return;
		}

		jockey.getRace().removeJockey(jockey);
        LangUtils.sendMessage(player, "command.leave.success");
	}

	/**
	 * Handle jockeys being kicked from the game
	 * 
	 * @param event The player kicked event
	 */
	@EventHandler
	public void onJockeyKick(PlayerKickEvent event) {

		Player player = event.getPlayer();
		Jockey jockey = MineKart.getInstance().getJockey(player);

		if (jockey == null) {
			return;
		}

		jockey.getRace().removeJockey(jockey);
        LangUtils.sendMessage(player, "command.leave.success");

	}

	/**
	 * Called when a player tried to dismount a vehicle.
	 * 
	 * @param event The vehicle exit event containing information on this
	 *            event
	 */
	@EventHandler
	public void onEntityDismount(VehicleExitEvent event) {

		LivingEntity jockeyEntity = event.getExited();

		if (!(jockeyEntity instanceof Player))
			return;

		Jockey jockey = MineKart.getInstance().getJockey((Player) jockeyEntity);
		if (jockey == null)
			return;

		event.setCancelled(true);
	}

	/**
	 * Called when a player interacts.
	 * 
	 * @param event The player interact event containing information on this
	 *            event
	 */
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {

		Player player = event.getPlayer();
		Jockey jockey = MineKart.getInstance().getJockey(player);

		if (jockey == null) {
			onPlayerInteractOutOfGame(player, event);
			return;
		}

		Race race = jockey.getRace();

		if (race.getState() == RaceState.InRace) {
			onJockeyInteractInGame(jockey, event);
		} else if (race.getState() == RaceState.Waiting) {
			onJockeyInteractInLobby(jockey, event);
		}

	}

	/**
	 * Perform actions for players that are not inside a racecourse
	 * 
	 * @param player the player that triggered this event
	 * @param event the player interact event, containing information on this
	 *            event
	 */
	private void onPlayerInteractOutOfGame(Player player, PlayerInteractEvent event) {

		if (!player.hasPermission("minekart.join")) {
            LangUtils.sendMessage(player, "command.error.permission", "minekart.join");
			return;
		}

		if (player.isSneaking()) {
			return;
		}

		Block block = event.getClickedBlock();

		if (block == null) {
			return;
		}

		LobbySign sign = LobbySignManager.getSignByLocation(block);

		if (sign == null) {
			return;
		}

        sign.onInteract(player);
	}

	/**
	 * Perform actions for players that are in a racecourse lobby
	 * 
	 * @param jockey the player that triggered this event
	 * @param event the player interact event, containing information on this
	 *            event
	 */
	private void onJockeyInteractInLobby(Jockey jockey, PlayerInteractEvent event) {

		Block clicked = event.getClickedBlock();
		Race race = jockey.getRace();

		if (clicked != null && clicked.getType() == race.getCourse().getReadyBlock()) {
			jockey.readyUp();
		}
	}

	/**
	 * Perform actions for players that are currently in a race
	 * 
	 * @param jockey the player that triggered this event
	 * @param event the player interact event, containing information on this
	 *            event
	 */
	@SuppressWarnings("deprecation")
	private void onJockeyInteractInGame(Jockey jockey, PlayerInteractEvent event) {

		if (jockey.getRace().hasJockeyFinished(jockey)) {
			event.setCancelled(true);
			return;
		}
		
		Player player = event.getPlayer();

		ItemStack item = player.getItemInHand();
		if (item == null || item.getItemMeta() == null || item.getItemMeta().getDisplayName() == null)
			return;

		if (item.getItemMeta().getDisplayName().equalsIgnoreCase("whip")) {

			int amount = item.getAmount();
			if (amount <= 1) {
				player.getInventory().removeItem(item);
			} else {
				item.setAmount(amount - 1);
			}
			player.updateInventory();

			jockey.increaseSpeed(2, 4); // strength, time
            LangUtils.sendMessage(player, "powerup.whip");
			jockey.getPlayer().playSound(player.getLocation(), getWhipSound(jockey.getMountType()), 2.0f, 1.0f);
			event.setCancelled(true);
			return;
		}

		if (item.getItemMeta().getDisplayName().equalsIgnoreCase("respawn")) {
			jockey.respawn();
            LangUtils.sendMessage(player, "powerup.respawned");
			player.getInventory().setHeldItemSlot(0);
			event.setCancelled(true);
			return;
		}

		if (player.getInventory().getHeldItemSlot() == Powerup.POWERUP_SLOT) {
			Powerup powerup = jockey.getPowerup();
			if (powerup == null) {
				return;
			}
			
			powerup.onUse(jockey);

			if (powerup.getAmount() <= 0) {
				player.getInventory().setItem(Powerup.POWERUP_SLOT, new ItemStack(Material.AIR));
			} else {
				item.setAmount(powerup.getAmount());
			}
			event.setCancelled(true);
			return;
		}

	}

	/**
	 * Get the sound that should be played when the mount is whiped
	 * 
	 * @param type The mount type
	 * @return The sound to be played
	 */
	private Sound getWhipSound(MountType type) {

		switch (type) {
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
				return Sound.HURT_FLESH;
		}
	}

	/**
	 * Called when a player moves.
	 * 
	 * @param event The player move event containing information on this event
	 */
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {

		Player player = event.getPlayer();
		Jockey jockey = MineKart.getInstance().getJockey(player);
		if (jockey == null)
			return;

		Race race = jockey.getRace();
		if (race.getState() == RaceState.Starting) {

        	final double edgeLimit = 0.3;
        	
        	final Location from = event.getFrom();
        	final Location to = event.getTo();

        	final double xDiff = to.getX() - to.getBlockX();
        	final double zDiff = to.getZ() - to.getBlockZ();
        	
            if (xDiff < edgeLimit || xDiff > (1.0 - edgeLimit) ||
        		zDiff < edgeLimit || zDiff > (1.0 - edgeLimit)) 
            {
            	from.setX(from.getBlockX() + 0.5);
            	from.setZ(from.getBlockZ() + 0.5);
            	event.setTo(from);
            }
			return;
		}
	
		if (race.getState() != RaceState.InRace)
			return;

		if (!jockey.hasMoved(player.getLocation()))
			return;

		race.onJockeyMove(jockey);
	}

	/**
	 * Called when a players hunger level changes.
	 * 
	 * @param event The player hunger change event containing information on
	 *            this event
	 */
	@EventHandler
	public void onPlayerHungerChange(FoodLevelChangeEvent event) {

		Player player = (Player) event.getEntity();
		Jockey jockey = MineKart.getInstance().getJockey(player);
		if (jockey == null)
			return;

		event.setCancelled(true);

	}

	/**
	 * Called when a player is damaged.
	 * 
	 * @param event The entity damage event containing information on this
	 *            event
	 */
	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {

		Entity entity = event.getEntity();
		if (!(entity instanceof Player)) {
			return;
		}

		Player player = (Player) entity;
		Jockey jockey = MineKart.getInstance().getJockey(player);
		if (jockey == null)
			return;

		if (event.getCause() == DamageCause.FALL) {
			if (player.hasPotionEffect(PotionEffectType.JUMP)) {
				NPC mount = jockey.getMount();
				if (mount == null) {
					event.setCancelled(true);
					return;
				}
				
				Mount trait = mount.getTrait(NmsHandler.getNmsHandler().getMountClass());
				if (trait != null && trait.getController().isJumping()){
					event.setCancelled(true);
					return;
				}
				
			}
		}
		
		Race race = jockey.getRace();
		if (race.getState() != RaceState.InRace) {
			event.setCancelled(true);
			return;
		}
		
		if (event instanceof EntityDamageByEntityEvent) {
			EntityDamageByEntityEvent entityAttack = (EntityDamageByEntityEvent)event;
			Entity damager = entityAttack.getDamager();
			
			// Other players can't attack
			if (damager instanceof Player) {
				event.setCancelled(true);
				return;
			}
			
			List<MetadataValue> powerupMetas = damager.getMetadata("powerup");
			for (MetadataValue meta : powerupMetas) {
				if (meta.value() instanceof Powerup) {
					Powerup powerup = (Powerup)meta.value();
					powerup.onDamageEntity(entityAttack);
				}
			}
			
		}
		
		if (event.getDamage() >= player.getHealth()) {
			event.setCancelled(true);
			jockey.respawn();
			return;
		}

	}

	/**
	 * Called when a player pickups up an item.
	 * 
	 * @param event The item pickup event containing information on this event
	 */
	@EventHandler
	public void onPickupItem(PlayerPickupItemEvent event) {

		final Player player = event.getPlayer();
		final Jockey jockey = MineKart.getInstance().getJockey(player);

		if (jockey == null) {
			return;
		}
		
		event.setCancelled(true);
		
		if (!jockey.canPickupPowerup()) {
			return;
		}
		
		final Item item = event.getItem();
		final ItemMeta meta = item.getItemStack().getItemMeta();

		if (meta == null || !meta.hasDisplayName() || !meta.getDisplayName().startsWith("Powerup")) {
			return;
		}

		ItemStack slotItem = player.getInventory().getItem(Powerup.POWERUP_SLOT);
		if (slotItem != null) {
			return;
		}

		item.remove();

		Powerup powerup = MineKart.getInstance().getRandomPowerup(jockey.getRace().getCourse());
		if (powerup == null) {
			return;
		}

		JockeyPowerupPickupEvent powerupEvent = new JockeyPowerupPickupEvent(jockey, jockey.getRace(), powerup);
		Bukkit.getPluginManager().callEvent(powerupEvent);

		jockey.getRace().getCourse().removePowerup(item.getLocation());
		powerupEvent.getPowerup().onPickup(jockey);
	}

	/**
	 * Called when a player drops an item.
	 * 
	 * @param event The item drop event containing information on this event
	 */
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onDropItem(PlayerDropItemEvent event) {

		final Player player = event.getPlayer();
		
		Jockey jockey = MineKart.getInstance().getJockey(player);
		if (jockey == null) {
			return;
		}

		PlayerInventory invent = player.getInventory();
		
		if (invent.getHeldItemSlot() == Powerup.POWERUP_SLOT) {
			jockey.setPowerup(null);
			event.getItemDrop().remove();
            LangUtils.sendMessage(player, "powerup.drop");
            return;
		}
		
		final int RESPAWN_SLOT = 8;
		if (invent.getHeldItemSlot() == RESPAWN_SLOT) {
			ItemStack stack = event.getItemDrop().getItemStack();
			event.getItemDrop().remove();
			invent.setItem(RESPAWN_SLOT, stack);
			return;
		}
		
		event.setCancelled(true);
		player.updateInventory();
	}

	/**
	 * Called when a player executes a command.
	 * 
	 * @param event The pre-process command event containing information on
	 *            this event
	 */
	@SuppressWarnings("unchecked")
	@EventHandler
	public void onPlayerCommand(PlayerCommandPreprocessEvent event) {

		final Player player = event.getPlayer();
		if (player.hasPermission("minekart.command.override")) {
			return;
		}

		Jockey jockey = MineKart.getInstance().getJockey(player);
		if (jockey == null) {
			return;
		}

		MineKart mineKart = MineKart.getInstance();
		Map<String, Map<String, Object>> commands = mineKart.getDescription().getCommands();

		final String requestedCommand = event.getMessage().substring(1);

		// Is it a command used by this plugin?
		for (Entry<String, Map<String, Object>> command : commands.entrySet()) {
			if (requestedCommand.startsWith(command.getKey())) {
				return;
			}

			List<String> allias = (List<String>) command.getValue().get("aliases");
			for (String alli : (List<String>) allias) {
				if (requestedCommand.startsWith(alli)) {
					return;
				}
			}
		}

        LangUtils.sendMessage(player, "command.error.ingame");
		event.setCancelled(true);
	}


}
