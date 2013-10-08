package uk.thecodingbadgers.minekart;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import uk.thecodingbadgers.minekart.command.CreateCommand;
import uk.thecodingbadgers.minekart.listener.CourseCreationListener;
import uk.thecodingbadgers.minekart.racecourse.RacecourceType;
import uk.thecodingbadgers.minekart.racecourse.Racecourse;
import uk.thecodingbadgers.minekart.racecourse.RacecourseCheckpoint;
import uk.thecodingbadgers.minekart.racecourse.RacecourseLap;

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
		
		registerListeners();
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
	
	/**
	 * Registers all listeners used by the plugin
	 */
	private void registerListeners() {
		PluginManager manager = this.getServer().getPluginManager();
		manager.registerEvents(new CourseCreationListener(), this);
	}
	
	/**
	 * Called when a command is to be handled
	 */
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		if (!command.getName().equalsIgnoreCase("minekart")) {
			return false;
		}
		
		// If no arguments are passed show the info and help
		if (args.length == 0) {
			showPluginHelp(sender);
			return true;
		}
		
		final String controlArgument = args[0];
		
		// if the control argument is create, let the create command handler take care of it
		if (controlArgument.equalsIgnoreCase("create") || controlArgument.equalsIgnoreCase("c")) {
			CreateCommand.handleCommand(sender, args);
			return true;
		}
		
		return true;
	}

	/**
	 * Shows the plugin help and information
	 */
	private void showPluginHelp(CommandSender sender) {
		
		PluginDescriptionFile pluginDescription = this.getDescription();
		
		MineKart.output(sender, "===================");
		MineKart.output(sender, ("MineKart by " + pluginDescription.getAuthors()).replaceAll("[", "").replaceAll("]", ""));
		MineKart.output(sender, "Version " + pluginDescription.getVersion());
		MineKart.output(sender, "===================");
		MineKart.output(sender, "/mk setup help");
		MineKart.output(sender, "/mk admin help");
		MineKart.output(sender, "/mk player help");
		
	}

	/**
	 * Output a message to a given command sender
	 */
	public static void output(CommandSender sender, String message) {
		sender.sendMessage(ChatColor.DARK_GREEN + "[MineKart] " + ChatColor.WHITE + message);
	}

	/**
	 * create a new arena
	 * @player The player who is creating the arena
	 * @name The name of the arena
	 * @type The type of arena to create
	 */
	public void createArena(Player player, String name, RacecourceType type) {
		
		Racecourse newCourse = null;
		switch(type) {
		case CheckPoint:
			{
				newCourse = new RacecourseCheckpoint();
				break;
			}
		
		
		case Lap:
			{
				newCourse = new RacecourseLap();
				break;
			}
		
		default:
			{
				MineKart.output(player, "This racecourse type is not yet supported.");
				MineKart.output(player, "Creation of '" + name + "' failed.");
				return;
			}
		}
		
		if (!newCourse.setup(player)) {
			MineKart.output(player, "Failed to setup new arena.");
			MineKart.output(player, "Creation of '" + name + "' failed.");
			return;
		}
		
		MineKart.output(player, "Created the new " + type + " arena '" + name + "' sucessfully!");
		MineKart.output(player, "Next you need too...");
		MineKart.output(player, " - Add spawn points");
		MineKart.output(player, " - Add checkpoints points");
		MineKart.output(player, " - Add a lobby spawn point");
		MineKart.output(player, " - Add a spectator spawn point");
	}
}
