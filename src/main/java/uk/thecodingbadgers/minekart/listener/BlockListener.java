package uk.thecodingbadgers.minekart.listener;

import java.util.Map;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;

import uk.thecodingbadgers.minekart.MineKart;
import uk.thecodingbadgers.minekart.lobby.LobbySign;
import uk.thecodingbadgers.minekart.lobby.LobbySignManager;
import uk.thecodingbadgers.minekart.racecourse.Racecourse;

/**
 * @author TheCodingBadgers
 * 
 *         This listener handles all block based event.
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
		Player player = event.getPlayer();
		LobbySign sign = LobbySignManager.getSignByLocation(block);

		if (sign != null && player.hasPermission("minekart.lobby.destroy")) {
			LobbySignManager.removeSign(sign);
			MineKart.output(player, "Successfully removed lobby sign");
		}

		Map<String, Racecourse> courses = MineKart.getInstance().getAllRacecourses();
		for (Racecourse course : courses.values()) {
			if (course.isEnabled() && course.isInCourseBounds(block.getLocation())) {
				event.setCancelled(true);
				return;
			}
		}

	}

	/**
	 * Called when a block is placed.
	 * 
	 * @param event The block place event containing information on this event
	 */
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {

		Block block = event.getBlock();

		Map<String, Racecourse> courses = MineKart.getInstance().getAllRacecourses();
		for (Racecourse course : courses.values()) {
			if (course.isEnabled() && course.isInCourseBounds(block.getLocation())) {
				event.setCancelled(true);
				return;
			}
		}

	}

	/**
	 * Called when a block is damaged.
	 * 
	 * @param event The block break event containing information on this event
	 */
	@EventHandler
	public void onBlockDamaged(BlockDamageEvent event) {

		final Block block = event.getBlock();

		Map<String, Racecourse> courses = MineKart.getInstance().getAllRacecourses();
		for (Racecourse course : courses.values()) {
			if (course.isInCourseBounds(block.getLocation())) {
				event.setCancelled(true);
				return;
			}
		}

	}

	/**
	 * Called when a sign is changed.
	 * 
	 * @param event The sign change event containing information on this event
	 */
	@EventHandler
	public void onSignChange(SignChangeEvent event) {
		Player player = event.getPlayer();

		if (!player.hasPermission("minekart.lobby.create")) {
			return;
		}

		if (!event.getLine(0).equalsIgnoreCase("[MineKart]")) {
			return;
		}

		String coursename = event.getLine(1);
		Racecourse course = MineKart.getInstance().getRacecourse(coursename);

		if (course == null) {
			return;
		}

		LobbySign sign = new LobbySign(event.getBlock(), course);
		LobbySignManager.addSign(sign);

		String[] lines = sign.getSignContent();

		for (int i = 0; i < 4; i++) {
			event.setLine(i, lines[i]);
		}

		MineKart.output(player, "You have created a lobby sign for " + course.getName());
	}

}
