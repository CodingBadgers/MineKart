package uk.thecodingbadgers.minekart.listener;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleExitEvent;

import uk.thecodingbadgers.minekart.MineKart;
import uk.thecodingbadgers.minekart.race.Jockey;

public class JockeyListener implements Listener {

	/**
	 * Called when a block is broken.
	 * 
	 * @param event The block break event containing information on this event
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
	
}
