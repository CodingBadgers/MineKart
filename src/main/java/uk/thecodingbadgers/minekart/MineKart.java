package uk.thecodingbadgers.minekart;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;

import org.apache.commons.io.IOUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;

import uk.thecodingbadgers.minekart.command.CommandHandler;
import uk.thecodingbadgers.minekart.jockey.Jockey;
import uk.thecodingbadgers.minekart.listener.BlockListener;
import uk.thecodingbadgers.minekart.listener.JockeyListener;
import uk.thecodingbadgers.minekart.mount.AgeableMountData;
import uk.thecodingbadgers.minekart.mount.HorseMountData;
import uk.thecodingbadgers.minekart.mount.MountDataRegistry;
import uk.thecodingbadgers.minekart.mount.SizeMountData;
import uk.thecodingbadgers.minekart.powerup.Powerup;
import uk.thecodingbadgers.minekart.powerup.PowerupDrop;
import uk.thecodingbadgers.minekart.powerup.PowerupPotion;
import uk.thecodingbadgers.minekart.powerup.PowerupProjectile;
import uk.thecodingbadgers.minekart.powerup.PowerupRegistry;
import uk.thecodingbadgers.minekart.racecourse.Racecourse;
import uk.thecodingbadgers.minekart.racecourse.RacecourseCheckpoint;
import uk.thecodingbadgers.minekart.racecourse.RacecourseLap;
import uk.thecodingbadgers.minekart.racecourse.RacecourseTypeRegistry;
import static uk.thecodingbadgers.minekart.lobby.LobbySignManager.loadSigns;

/**
 * @author TheCodingBadgers
 * 
 *         Main entry class for the MineKart plugin
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

	/** The path to the folder where all powerups reside */
	private static File powerupFolderPath = null;

	/** The path to the folder where all lobby signs reside */
	private static File lobbyFolderPath = null;

	/** All available powerups */
	private List<Powerup> powerups = null;

	/** Powerup type registry */
	private PowerupRegistry powerupRegistry;

	/** Racecourse type registry */
	private RacecourseTypeRegistry racecourseTypeRegistry;

	/** Mount data registry */
	private MountDataRegistry mountDataRegistry;

	/**
	 * Called when the plugin is enabled
	 */
	public void onEnable() {

		// Store the instance of the plugin
		MineKart.instance = this;

		/* Setup plugin folder */

		// Setup the folder which will hold all the racecourse configs
		MineKart.racecourseFolderPath = new File(this.getDataFolder() + File.separator + "courses");
		if (!MineKart.racecourseFolderPath.exists()) {
			MineKart.racecourseFolderPath.mkdirs();
		}

		// Setup the folder which will hold all the powerups configs
		MineKart.powerupFolderPath = new File(this.getDataFolder() + File.separator + "powerups");
		if (!MineKart.powerupFolderPath.exists()) {
			MineKart.powerupFolderPath.mkdirs();
			copyDefaultPowerups(); // extract default configs from jar
		}

		// Setup the folder which will hold all the lobby signs configs
		MineKart.lobbyFolderPath = new File(this.getDataFolder() + File.separator + "signs");
		if (!MineKart.lobbyFolderPath.exists()) {
			MineKart.lobbyFolderPath.mkdirs();
		}

		PluginManager pluginManager = this.getServer().getPluginManager();

		// Get the world edit plugin instance
		this.worldEdit = (WorldEditPlugin) pluginManager.getPlugin("WorldEdit");
		if (this.worldEdit == null) {
			getLogger().log(Level.SEVERE, "Could not find the WorldEdit plugin.");
		}

		// Register powerup types
		this.powerupRegistry = new PowerupRegistry();
		this.powerupRegistry.registerPowerupType("potion", PowerupPotion.class);
		this.powerupRegistry.registerPowerupType("projectile", PowerupProjectile.class);
		this.powerupRegistry.registerPowerupType("drop", PowerupDrop.class);

		this.racecourseTypeRegistry = new RacecourseTypeRegistry();
		this.racecourseTypeRegistry.registerRacecourseType("lap", RacecourseLap.class);
		this.racecourseTypeRegistry.registerRacecourseType("checkpoint", RacecourseCheckpoint.class);

		this.mountDataRegistry = new MountDataRegistry();
		this.mountDataRegistry.registerCustomMountData(EntityType.HORSE, HorseMountData.class);
		this.mountDataRegistry.registerCustomMountData(EntityType.SLIME, SizeMountData.class);
		this.mountDataRegistry.registerCustomMountData(EntityType.MAGMA_CUBE, SizeMountData.class);
		this.mountDataRegistry.registerCustomMountData(EntityType.COW, AgeableMountData.class);
		this.mountDataRegistry.registerCustomMountData(EntityType.CHICKEN, AgeableMountData.class);
		this.mountDataRegistry.registerCustomMountData(EntityType.SHEEP, AgeableMountData.class);
		this.mountDataRegistry.registerCustomMountData(EntityType.OCELOT, AgeableMountData.class);
		this.mountDataRegistry.registerCustomMountData(EntityType.PIG, AgeableMountData.class);
		this.mountDataRegistry.registerCustomMountData(EntityType.ZOMBIE, AgeableMountData.class);
		this.mountDataRegistry.registerCustomMountData(EntityType.WOLF, AgeableMountData.class);

		registerListeners();

		getCommand("minekart").setExecutor(new CommandHandler());

		this.courses = new HashMap<String, Racecourse>();

		loadPowerups();
		loadRacecourses();
		loadSigns();
	}

	/**
	 * Called when the plugin is disabled
	 */
	public void onDisable() {
		// Reset the instance on disable
		MineKart.instance = null;

		for (Racecourse course : this.courses.values()) {
			course.getRace().end();
		}
	}

	/**
	 * Gets the active instance of the MineKart plugin.
	 * 
	 * @return The instance of the MineKart plugin, or null if the plugin
	 *         isn't enabled.
	 */
	public static MineKart getInstance() {
		return MineKart.instance;
	}

	/**
	 * Gets the WorldEdit plugin instance.
	 * 
	 * @return The instance of the WorldEdit plugin.
	 */
	public WorldEditPlugin getWorldEditPlugin() {
		return this.worldEdit;
	}

	/**
	 * Gets the powerup registry.
	 * 
	 * @return the powerup registry
	 */
	public PowerupRegistry getPowerupRegistry() {
		return this.powerupRegistry;
	}

	/**
	 * Gets the mount data registry.
	 * 
	 * @return the mount data registry
	 */
	public MountDataRegistry getMountDataRegistry() {
		return this.mountDataRegistry;
	}

	/**
	 * Gets the racecourse type registry.
	 * 
	 * @return the racecourse type registry
	 */
	public RacecourseTypeRegistry getRacecourseTypeRegistry() {
		return this.racecourseTypeRegistry;
	}

	/**
	 * Get the folder of which all racecourse configs reside
	 * 
	 * @return The folder where the racecourse configs should be
	 */
	public static File getRacecourseFolder() {
		return MineKart.racecourseFolderPath;
	}

	/**
	 * Get the folder of which all lobby signs reside
	 * 
	 * @return The folder where the lobby signs should be
	 */
	public static File getLobbyFolder() {
		return MineKart.lobbyFolderPath;
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
	 * Load all powerups
	 */
	private void loadPowerups() {
		this.powerups = new ArrayList<Powerup>();

		File[] powerupFiles = MineKart.powerupFolderPath.listFiles();
		for (File file : powerupFiles) {
			final String filename = file.getName();

			if (!filename.endsWith(".yml"))
				continue;

			final String[] nameparts = filename.split("\\.");

			final String powerupname = nameparts[0];
			final String poweruptype = nameparts[1];

			Powerup powerup = getPowerupRegistry().getPowerupType(poweruptype);

			if (powerup == null) {
				getLogger().log(Level.SEVERE, "Unknown powerup type '" + poweruptype + "' for powerup '" + powerupname + "'.");
				continue;
			}

			powerup.load(file);
			this.powerups.add(powerup);
			getLogger().log(Level.INFO, "Loaded powerup: " + powerupname);

		}
	}

	/**
	 * Copy the default powerups out of the jar and into the powerups
	 * directory.
	 */
	private void copyDefaultPowerups() {
		JarFile file = null;
		JarEntry entry = null;

		try {
			file = new JarFile(getFile());

			for (Enumeration<JarEntry> em = file.entries(); em.hasMoreElements();) {
				entry = em.nextElement();
				String s = entry.toString();

				if (s.startsWith(("powerups/"))) {
					String fileName = s.substring(s.lastIndexOf("/") + 1, s.length());
					if (fileName.endsWith(".yml")) {
						File powerupFile = new File(powerupFolderPath, fileName);
						InputStream inStream = file.getInputStream(entry);
						OutputStream out = new FileOutputStream(powerupFile);

						IOUtils.copy(inStream, out);
						IOUtils.closeQuietly(inStream);
						IOUtils.closeQuietly(out);
					}
				}
			}

		} catch (IOException e) {
			getLogger().log(Level.SEVERE, "Error copying default configs from jar", e);
		} finally {
			if (file != null) {
				try {
					file.close();
				} catch (IOException e) {
				}
			}
		}
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

			Racecourse course = this.racecourseTypeRegistry.createRacecourse(coursetype);

			if (course == null) {
				getLogger().log(Level.SEVERE, "Unknown course type '" + coursetype + "' for course '" + coursename + "'.");
				continue;
			}

			course.load(file);
			this.courses.put(coursename.toLowerCase(), course);
			getLogger().log(Level.INFO, "Loaded racecourse: " + coursename);
		}
	}

	/**
	 * Output a message to a given command sender
	 */
	public static void output(CommandSender sender, String message) {
		if (MineKart.getInstance() == null) {
			return;
		}

		sender.sendMessage(ChatColor.DARK_GREEN + "[MineKart] " + ChatColor.WHITE + message);
	}

	/**
	 * Output a message to a given command sender from a given player
	 */
	public static void output(CommandSender to, CommandSender from, String message) {
		if (MineKart.getInstance() == null) {
			return;
		}

		to.sendMessage(ChatColor.DARK_GREEN + "[MineKart] " + ChatColor.YELLOW + "[" + from.getName() + "] " + ChatColor.WHITE + message);
	}

	/**
	 * create a new arena
	 * 
	 * @param player The player who is creating the arena
	 * @param name The name of the arena
	 * @param type The type of arena to create
	 */
	public void createCourse(Player player, String name, String type) {

		if (this.courses.containsKey(name.toLowerCase())) {
			MineKart.output(player, "A racecourse with this name already exists.");
			MineKart.output(player, "Creation of '" + name + "' failed.");
			return;
		}

		Racecourse newCourse = this.racecourseTypeRegistry.createRacecourse(type);

		if (newCourse == null) {
			MineKart.output(player, "This racecourse type is not yet supported.");
			MineKart.output(player, "Creation of '" + name + "' failed.");
			return;
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
	 * Delete a racecourse
	 * 
	 * @param sender The thing executing the command
	 * @param course The course to delete.
	 */
	public void deleteCourse(CommandSender sender, Racecourse course) {

		// remove it from memory
		this.courses.remove(course);

		// remove it from file
		course.delete();

		sender.sendMessage("The racecourse '" + course.getName() + "' has been deleted...");
		course = null;

	}

	/**
	 * Get a course from a given name
	 * 
	 * @param courseName The name of the course to get
	 * @return The course represented by the given name, or null if a course
	 *         was not found.
	 */
	public Racecourse getRacecourse(String courseName) {

		if (this.courses.containsKey(courseName))
			return this.courses.get(courseName.toLowerCase());

		for (String name : this.courses.keySet()) {
			if (name.startsWith(courseName.toLowerCase())) {
				return this.courses.get(name.toLowerCase());
			}
		}

		return null;
	}

	/**
	 * Get the racecourse map
	 * 
	 * @return The map of all known racecourses.
	 */
	public Map<String, Racecourse> getAllRacecourses() {
		return this.courses;
	}

	/**
	 * Get the jockey that represents a given player
	 * 
	 * @param player The player to get the jockey of
	 * @return The jockey instance, or null if the given player isn't a jockey
	 *         in any race
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
	 * Converts milliseconds into user friendly time format
	 * 
	 * @param raceTime The time to convert
	 * @return A string representing the time in a nice format
	 */
	public static String formatTime(long raceTime) {
		Date date = new Date(raceTime);
		DateFormat formatter = new SimpleDateFormat("mm:ss:SS");
		return formatter.format(date);
	}

	/**
	 * Get a random powerup
	 * 
	 * @param course The race course the powerup will be long too.
	 * @return The random powerup instance
	 */
	public Powerup getRandomPowerup(Racecourse course) {

		if (powerups.isEmpty()) {
			return null;
		}

		List<Powerup> allowedPowerups = new ArrayList<Powerup>();
		for (Powerup p : powerups) {
			if (course.getPowerupBlackList().contains(p.getName().toLowerCase())) {
				continue;
			}

			allowedPowerups.add(p);
		}

		Random random = new Random();
		Powerup powerup = allowedPowerups.get(random.nextInt(allowedPowerups.size()));

		return this.powerupRegistry.clonePowerup(powerup);
	}
}
