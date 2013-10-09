package uk.thecodingbadgers.minekart.listener;

import java.util.Map;

import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;

import uk.thecodingbadgers.minekart.MineKart;
import uk.thecodingbadgers.minekart.racecourse.Racecourse;

/**
 * @author TheCodingBadgers
 *
 * This listener handles all block based event.
 *
 */
public class BlockListener implements Listener {

	/**
	 * Called when a block is broken.
	 * 
	 * @param event The block break event containing information on this event
	 */
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {

		Block block = event.getBlock();
		
		Map<String, Racecourse> courses = MineKart.getInstance().getAllRacecourses();
		for (Racecourse course : courses.values()) {
			if (course.isInCourseBounds(block.getLocation())) {
				event.setCancelled(true);
				return;
			}
		}
		
	}
	
	/**
	 * Called when a block is broken.
	 * 
	 * @param event The block break event containing information on this event
	 */
	@EventHandler
	public void onBlockBreak(BlockDamageEvent event) {

		Block block = event.getBlock();

		Map<String, Racecourse> courses = MineKart.getInstance().getAllRacecourses();
		for (Racecourse course : courses.values()) {
			if (course.isInCourseBounds(block.getLocation())) {
				event.setCancelled(true);
				return;
			}
		}
		
	}
	
}
