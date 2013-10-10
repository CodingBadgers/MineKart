package uk.thecodingbadgers.minekart;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;

import uk.thecodingbadgers.minekart.command.CreateCommand;
import uk.thecodingbadgers.minekart.command.HelperCommand;
import uk.thecodingbadgers.minekart.command.RaceCommand;
import uk.thecodingbadgers.minekart.listener.BlockListener;
import uk.thecodingbadgers.minekart.listener.JockeyListener;
import uk.thecodingbadgers.minekart.race.Jockey;
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
	
	/** The instance of the MineKart plugin */
	private static MineKart instance = null;
	
	/** Access to the world edit plugin */
	private WorldEditPlugin worldEdit = null;
	
	/** Map of all known racecourses where the key is the course name */
	private Map<String, Racecourse> courses = null;
	
	/** The path to the folder where all racecourses reside */
	private static File racecourseFolderPath = null;
	
	/**
	 * Called when the plugin is enabled
	 */
	public void onEnable() {
		
		// Store the instance of the plugin
		MineKart.instance = this;
		
		// Setup the folder which will hold all the racecourse configs
		MineKart.racecourseFolderPath = new File(this.getDataFolder() + File.separator + "courses");
		if (!MineKart.racecourseFolderPath.exists()) {
			MineKart.racecourseFolderPath.mkdirs();
		}
		
		PluginManager pluginManager = this.getServer().getPluginManager();
		
		// Get the world edit plugin instance
		this.worldEdit = (WorldEditPlugin)pluginManager.getPlugin("WorldEdit");
		if (this.worldEdit == null) {
			Bukkit.getLogger().log(Level.SEVERE, "Could not find the WorldEdit plugin.");
		}
		
		registerListeners();
		
		this.courses = new HashMap<String, Racecourse>();
		
		loadRacecourses();
	}

	/**
	 * Called when the plugin is disabled
	 */
	public void onDisable() {
		MineKart.instance = null;
		
		for (Racecourse course : this.courses.values()) {
			course.getRace().end();
		}
	}
	
	/**
	 * Gets the active instance of the MineKart plugin.
	 * @return The instance of the MineKart plugin.
	 */
	public static MineKart getInstance() {
		return MineKart.instance;
	}
	
	/**
	 * Gets the WorldEdit plugin instance.
	 * @return The instance of the WorldEdit plugin.
	 */
	public WorldEditPlugin getWorldEditPlugin() {
		return this.worldEdit;
	}
	
	/**
	 * Get the folder of which all racecourse configs reside
	 * @return The folder where the racecourse configs should be
	 */
	public static File getRacecourseFolder() {
		return MineKart.racecourseFolderPath;
	}
	
	/**
	 * Registers all listeners used by the plugin
	 */
	private void registerListeners() {
		PluginManager manager = this.getServer().getPluginManager();
		manager.registerEvents(new BlockListener(), this);
		manager.registerEvents(new JockeyListener(), this);
	}
	
	/**
	 * Load all racecourses
	 */
	private void loadRacecourses() {
		File[] coursefiles = MineKart.racecourseFolderPath.listFiles();
		
		for (File file : coursefiles) {
			final String filename = file.getName();
			
			if (!filename.endsWith(".yml"))
				continue;
			
			final String[] nameparts = filename.split("\\.");
			
			final String coursename = nameparts[0];
			final String coursetype = nameparts[1];
			
			Racecourse course = null;
			if (coursetype.equalsIgnoreCase("lap")) {
				course = new RacecourseLap();
			}
			else if (coursetype.equalsIgnoreCase("checkpoint")) {
				course = new RacecourseCheckpoint();
			}
			
			if (course == null) {
				Bukkit.getLogger().log(Level.SEVERE, "Unknown course type '" + coursetype + "' for course '" + coursename + "'.");
				continue;
			}
			
			course.load(file);
			this.courses.put(coursename.toLowerCase(), course);
			Bukkit.getLogger().log(Level.INFO, "Loaded racecourse: " + coursename);
		}
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
		
		final String controlArgument = args[0].toLowerCase();
		
		// if the control argument is create, let the create command handler take care of it
		if (controlArgument.equalsIgnoreCase("create") || controlArgument.equalsIgnoreCase("c")) {
			CreateCommand.handleCreateCommand(sender, args);
			return true;
		}
		
		// if the control argument is set or add, let the setwarp command handler take care of it
		if (controlArgument.startsWith("set") || controlArgument.startsWith("add")) {
			CreateCommand.handleSetWarpCommand(sender, args);
			return true;
		}
		
		// if the control argument is list, let the helper command handler take care of it
		if (controlArgument.startsWith("list")) {
			HelperCommand.handleListCommand(sender, args);
			return true;
		}
		
		// if the control argument is info, let the helper command handler take care of it
		if (controlArgument.startsWith("info")) {
			HelperCommand.handleInfoCommand(sender, args);
			return true;
		}
		
		// if the control argument is join, let the race command handler take care of it
		if (controlArgument.startsWith("join") || controlArgument.startsWith("j")) {
			RaceCommand.handleJoinCommand(sender, args);
			return true;
		}
		
		// if the control argument is leave, let the race command handler take care of it
		if (controlArgument.startsWith("leave") || controlArgument.startsWith("l")) {
			RaceCommand.handleLeaveCommand(sender, args);
			return true;
		}
		
		// if the control argument is forcestart, let the race command handler take care of it
		if (controlArgument.startsWith("forcestart")) {
			RaceCommand.handleForceStartCommand(sender, args);
			return true;
		}
		
		// Unknown command
		showPluginHelp(sender);
		return true;
	}

	/**
	 * Shows the plugin help and information
	 */
	private void showPluginHelp(CommandSender sender) {
		
		PluginDescriptionFile pluginDescription = this.getDescription();
		
		MineKart.output(sender, "===================");
		MineKart.output(sender, "MineKart by " + pluginDescription.getAuthors());
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
	 * Output a message to a given command sender from a given player
	 */
	public static void output(CommandSender to, CommandSender from, String message) {
		to.sendMessage(ChatColor.DARK_GREEN + "[MineKart] " + ChatColor.YELLOW + "[" + from.getName() + "] " + ChatColor.WHITE + message);
	}

	/**
	 * create a new arena
	 * @player The player who is creating the arena
	 * @name The name of the arena
	 * @type The type of arena to create
	 */
	public void createArena(Player player, String name, RacecourceType type) {
		
		if (this.courses.containsKey(name.toLowerCase())) {
			MineKart.output(player, "A racecourse with this name already exists.");
			MineKart.output(player, "Creation of '" + name + "' failed.");
			return;
		}
		
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
		
		if (!newCourse.setup(player, name)) {
			MineKart.output(player, "Failed to setup new arena.");
			MineKart.output(player, "Creation of '" + name + "' failed.");
			return;
		}
		
		this.courses.put(name.toLowerCase(), newCourse);
		
		MineKart.output(player, "Created the new " + type + " arena '" + name + "' sucessfully!");
		MineKart.output(player, "Next you need to...");
		newCourse.outputRequirements(player);
				
	}
	
	/**
	 * Get a course from a given name
	 * @param courseName The name of the course to get
	 * @return The course represented by the given name, or null if a course was not found.
	 */
	public Racecourse getRacecourse(String courseName) {
		return this.courses.get(courseName.toLowerCase());
	}
	
	/**
	 * Get the racecourse map
	 * @return The map of all known racecourses.
	 */
	public Map<String, Racecourse> getAllRacecourses() {
		return this.courses;
	}

	/**
	 * Get the jockey that represents a given player
	 * @param player The player to get the jockey of
	 * @return The jockey instance, or null if the given player isn't a jockey in any race
	 */
	public Jockey getJockey(Player player) {
		
		for (Racecourse course : this.courses.values()) {
			Jockey jockey = course.getRace().getJockey(player);
			if (jockey != null)
				return jockey;
		}
		
		return null;
	}

	/**
	 * 
	 * @param raceTime
	 * @return
	 */
	public static String formatTime(long raceTime) {
		Date date = new Date(raceTime);
		DateFormat formatter = new SimpleDateFormat("mm:ss:SS");
		return formatter.format(date);
	}
}
