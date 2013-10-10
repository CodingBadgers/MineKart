package uk.thecodingbadgers.minekart.listener;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.inventory.ItemStack;

import uk.thecodingbadgers.minekart.MineKart;
import uk.thecodingbadgers.minekart.race.Jockey;

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
		
		ItemStack item = player.getItemInHand();
		if (item.getItemMeta().getDisplayName().equalsIgnoreCase("whip")) {	
			int amount = item.getAmount() - 1;
			item.setAmount(amount);
			if (amount <= 0) {
				player.getInventory().removeItem(item);
			}
			player.updateInventory();
			
			jockey.increaseSpeed(4, 5); // strength, time
			jockey.getRace().outputToRace(jockey.getPlayer(), "hYah!");
		}
		
	}
	
}
