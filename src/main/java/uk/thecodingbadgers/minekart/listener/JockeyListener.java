package uk.thecodingbadgers.minekart.listener;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.inventory.ItemStack;

import uk.thecodingbadgers.minekart.MineKart;
import uk.thecodingbadgers.minekart.race.Jockey;
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
			
			jockey.increaseSpeed(4, 5); // strength, time
			jockey.getRace().outputToRace(jockey.getPlayer(), "hYah!");
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

		race.onJockeyMove(jockey);
	}
}
