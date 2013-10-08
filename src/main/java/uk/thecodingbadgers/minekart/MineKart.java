package uk.thecodingbadgers.minekart;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author TheCodingBadgers
 *
 * Main entry class for the MineKart plugin
 *
 */
public final class MineKart extends JavaPlugin {
	 
	private static MineKart instance = null;
	
	/**
	 * Called when the plugin is enabled
	 */
	public void onEnable() {
		MineKart.instance = this;
	}
 
	/**
	 * Called when the plugin is disabled
	 */
	public void onDisable() {
		MineKart.instance = null;
	}
	
	/**
	 * Gets the active instance of the MineKart plugin.
	 * @return The instance of the MineKart plugin.
	 */
	public static MineKart getInstance() {
		return instance;
	}
}
