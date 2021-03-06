package uk.thecodingbadgers.minekart;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
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

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.libs.com.google.gson.Gson;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.io.ByteStreams;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;

import uk.thecodingbadgers.minekart.command.CommandHandler;
import uk.thecodingbadgers.minekart.jockey.Jockey;
import uk.thecodingbadgers.minekart.jockey.JockeyDataManager;
import uk.thecodingbadgers.minekart.lang.LangUtils;
import uk.thecodingbadgers.minekart.listener.BlockListener;
import uk.thecodingbadgers.minekart.listener.JockeyListener;
import uk.thecodingbadgers.minekart.mount.*;
import uk.thecodingbadgers.minekart.powerup.Powerup;
import uk.thecodingbadgers.minekart.powerup.PowerupDrop;
import uk.thecodingbadgers.minekart.powerup.PowerupPotion;
import uk.thecodingbadgers.minekart.powerup.PowerupProjectile;
import uk.thecodingbadgers.minekart.powerup.PowerupRegistry;
import uk.thecodingbadgers.minekart.powerup.damageeffect.DamageEffect;
import uk.thecodingbadgers.minekart.powerup.damageeffect.DamageEffectFreeze;
import uk.thecodingbadgers.minekart.powerup.damageeffect.DamageEffectIgnite;
import uk.thecodingbadgers.minekart.powerup.damageeffect.DamageEffectPoison;
import uk.thecodingbadgers.minekart.powerup.damageeffect.DamageEffectShrink;
import uk.thecodingbadgers.minekart.racecourse.Racecourse;
import uk.thecodingbadgers.minekart.racecourse.RacecourseCheckpoint;
import uk.thecodingbadgers.minekart.racecourse.RacecourseLap;
import uk.thecodingbadgers.minekart.racecourse.RacecourseTypeRegistry;
import uk.thecodingbadgers.minekart.userstats.StatsManager;
import uk.thecodingbadgers.minekart.version.NmsHandler;
import static uk.thecodingbadgers.minekart.version.NmsHandler.setupNMSHandling;
import static uk.thecodingbadgers.minekart.lobby.LobbySignManager.loadSigns;

/**
 * @author TheCodingBadgers
 * 
 *         Main entry class for the MineKart plugin
 * 
 */
public final class MineKart extends JavaPlugin {

	private static MineKart instance = null;
	private WorldEditPlugin worldEdit = null;
	
	private static File racecourseFolderPath = null;
	private static File powerupFolderPath = null;
	private static File lobbyFolderPath = null;
	private static File nmsHandlersPath = null;
	private static File langFolderPath = null;

	private List<Powerup> powerups = null;
	private Map<String, DamageEffect> damageEffects;
	private StatsManager statsManager;
	private Map<String, Racecourse> courses = null;
	
	private RacecourseTypeRegistry racecourseTypeRegistry;
	private MountDataRegistry mountDataRegistry;
	private JockeyDataManager jockeyDataManager;
	private PowerupRegistry powerupRegistry;

	/**
	 * Called when the plugin is being loaded
	 */
	public void onLoad() {
		// Store the instance of the plugin
		MineKart.instance = this;

		// Setup plugin data folder
		setupDataFolder();

		// Setup Bukkit versioned calls handling
		if (!setupNMSHandling()) {
			MineKart.getInstance().getLogger().log(Level.SEVERE, "NMS handling could not be setup, disabling plugin");
			setEnabled(false);
			return;
		}
		
		LangUtils.setupLanguages();

		// Setup registries
		this.powerupRegistry = new PowerupRegistry();
		this.racecourseTypeRegistry = new RacecourseTypeRegistry();
		this.mountDataRegistry = new MountDataRegistry();
		this.damageEffects = new HashMap<String, DamageEffect>();

		this.courses = new HashMap<String, Racecourse>();
		this.statsManager = new StatsManager();
	}
	
	/**
	 * Called when the plugin is enabled
	 */
	public void onEnable() {
		// Get the world edit plugin instance
		this.worldEdit = (WorldEditPlugin) this.getServer().getPluginManager().getPlugin("WorldEdit");
		if (this.worldEdit == null) {
			getLogger().log(Level.SEVERE, "Could not find the WorldEdit plugin.");
		}

		// Register powerup types
		this.powerupRegistry.registerPowerupType("potion", PowerupPotion.class);
		this.powerupRegistry.registerPowerupType("projectile", PowerupProjectile.class);
		this.powerupRegistry.registerPowerupType("drop", PowerupDrop.class);

		// Register racecourse types
		this.racecourseTypeRegistry.registerRacecourseType("lap", RacecourseLap.class);
		this.racecourseTypeRegistry.registerRacecourseType("checkpoint", RacecourseCheckpoint.class);

		// Register mount data types
		this.mountDataRegistry.registerCustomMountData(MountType.HORSE, HorseMountData.class);
		this.mountDataRegistry.registerCustomMountData(MountType.SLIME, SizeMountData.class);
		this.mountDataRegistry.registerCustomMountData(MountType.MAGMA_CUBE, SizeMountData.class);
		this.mountDataRegistry.registerCustomMountData(MountType.COW, AgeableMountData.class);
		this.mountDataRegistry.registerCustomMountData(MountType.CHICKEN, AgeableMountData.class);
		this.mountDataRegistry.registerCustomMountData(MountType.SHEEP, AgeableMountData.class);
		this.mountDataRegistry.registerCustomMountData(MountType.OCELOT, AgeableMountData.class);
		this.mountDataRegistry.registerCustomMountData(MountType.PIG, AgeableMountData.class);
		this.mountDataRegistry.registerCustomMountData(MountType.ZOMBIE, AgeableMountData.class);
		this.mountDataRegistry.registerCustomMountData(MountType.WOLF, AgeableMountData.class);
        this.mountDataRegistry.registerCustomMountData(MountType.FOOT, FootMountData.class);

		// Register damage effects
		this.damageEffects.put("ignite", new DamageEffectIgnite());
		this.damageEffects.put("poison", new DamageEffectPoison());
		this.damageEffects.put("freeze", new DamageEffectFreeze());
		this.damageEffects.put("shrink", new DamageEffectShrink());
				
		// Register listeners
		registerListeners();

		// Setup command handling
		getCommand("minekart").setExecutor(new CommandHandler());

		// Load data from disk
		loadJockeyData();
		loadPowerups();
		loadRacecourses();
		loadSigns(MineKart.getLobbyFolder());
	}

	private void setupDataFolder() {
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

		// Setup the folder which will hold all the lobby signs configs
		MineKart.nmsHandlersPath = new File(this.getDataFolder() + File.separator + "nms");
		if (!MineKart.nmsHandlersPath.exists()) {
			MineKart.nmsHandlersPath.mkdirs();
		}

		// Setup the folder which will hold all the lobby signs configs
		MineKart.langFolderPath  = new File(this.getDataFolder() + File.separator + "lang");
		if (!MineKart.langFolderPath.exists()) {
			MineKart.langFolderPath.mkdirs();
			copyDefaultLanguages(); // extract default lang files from jar
		}
	}

	/**
	 * Called when the plugin is disabled
	 */
	public void onDisable() {
        // End all races
		if (this.courses != null) {
			for (Racecourse course : this.courses.values()) {
				course.getRace().end();
			}
		}
        
        // Reset the instance on disable
		MineKart.instance = null;
		NmsHandler.cleanup();
	}
	
	/**
	 * Reload all configs
	 */
	public void reload() {
		
		for (Racecourse course : this.courses.values()) {
			course.getRace().end();
		}
		
		this.courses.clear();		
		this.powerups.clear();
		
		NmsHandler.cleanup();
		this.jockeyDataManager = null;
		
		NmsHandler.setupNMSHandling();
		this.loadPowerups();
		this.loadRacecourses();		
		this.loadJockeyData();
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
	 * Get the jockey data manager
	 * 
	 * @return The jockey data manager
	 */
	public JockeyDataManager getJockeyDataManager() {
		return this.jockeyDataManager;
	}
	
	/**
	 * Get the stats manager instance
	 * @return The instance of the stats manager
	 */
	public StatsManager getStatsManager() {
		return this.statsManager;
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
	 * Get the folder of which all nms handler jars reside
	 * 
	 * @return The folder where the nms handler jars should be
	 */
	public static File getNmsFolder() {
		return MineKart.nmsHandlersPath;
	}

	/**
	 * Get the folder of which all language files reside
	 * 
	 * @return The folder where the language files should be
	 */
	public static File getLangFolder() {
		return MineKart.langFolderPath;
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
						
						ByteStreams.copy(inStream, out);
						close(inStream);
						close(out);
					}
				}
			}

		} catch (IOException e) {
			getLogger().log(Level.SEVERE, "Error copying default configs from jar", e);
		} finally {
			close(file);
		}
	}

	/**
	 * Copy the default powerups out of the jar and into the powerups
	 * directory.
	 */
	private void copyDefaultLanguages() {
		JarFile file = null;
		JarEntry entry = null;

		try {
			file = new JarFile(getFile());

			for (Enumeration<JarEntry> em = file.entries(); em.hasMoreElements();) {
				entry = em.nextElement();
				String s = entry.toString();

				if (s.startsWith(("lang/"))) {
					String fileName = s.substring(s.lastIndexOf("/") + 1, s.length());
					if (fileName.endsWith(".lang")) {
						File langFile = new File(langFolderPath, fileName);
						InputStream inStream = file.getInputStream(entry);
						OutputStream out = new FileOutputStream(langFile);
						
						ByteStreams.copy(inStream, out);
						close(inStream);
						close(out);
					}
				}
			}

		} catch (IOException e) {
			getLogger().log(Level.SEVERE, "Error copying default configs from jar", e);
		} finally {
			close(file);
		}
	}
	
	private void close(Closeable close) {
		if (close != null) {
			try {
				close.close();
			} catch (Exception ex) {
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
			
			try {
				
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
				
			}  catch (Exception ex) {
				getLogger().log(Level.WARNING, "The racecourse with the file name '" + filename + "' failed to load correctly.", ex);
			}
		}
	}
	
	private void loadJockeyData() {
		File file = new File(this.getDataFolder(), "data.json");
		
		if (!file.exists()) {
			try {
				file.createNewFile();
				InputStream inStream = getResource("data.json");
				OutputStream out = new FileOutputStream(file);
				
				ByteStreams.copy(inStream, out);
				close(inStream);
				close(out);
			} catch (IOException ex) {
				getLogger().log(Level.SEVERE, "Could not load jockey data.", ex);
				return;
			}
		}
		
		try {
			Gson gson = new Gson();
			jockeyDataManager = gson.fromJson(new FileReader(file), JockeyDataManager.class);
		} catch (Exception e) {
			getLogger().log(Level.SEVERE, "Could not load jockey data.", e);
		}
	}

	/**
	 * Output a message to a given command sender
	 * 
	 * @deprecated {@link LangUtils#sendMessage(CommandSender, uk.thecodingbadgers.minekart.lang.Lang, String, Object...)}
	 */
	public static void output(CommandSender sender, String message) {
		if (MineKart.getInstance() == null) {
			return;
		}

		sender.sendMessage(ChatColor.DARK_GREEN + "[MineKart] " + ChatColor.WHITE + message);
	}

	/**
	 * Output a message to a given command sender from a given player
	 * 
	 * @deprecated {@link LangUtils#sendMessage(CommandSender, uk.thecodingbadgers.minekart.lang.Lang, String, Object...)}
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
            LangUtils.sendMessage(player, "course.create.error.exists");
            LangUtils.sendMessage(player, "course.create.error", name);
			return;
		}

		Racecourse newCourse = this.racecourseTypeRegistry.createRacecourse(type);

		if (newCourse == null) {
            LangUtils.sendMessage(player, "course.create.error.notsupported", type);
            LangUtils.sendMessage(player, "course.create.error", name);
			return;
		}

		if (!newCourse.setup(player, name)) {
            LangUtils.sendMessage(player, "course.create.error.fail", name);
            LangUtils.sendMessage(player, "course.create.error", name);
			return;
		}

		this.courses.put(name.toLowerCase(), newCourse);

        LangUtils.sendMessage(player, "course.create.success", type, name);
        LangUtils.sendMessage(player, "course.create.next");
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


        LangUtils.sendMessage(sender, "course.delete.success", course.getName());
		course = null;

	}

	/**
	 * Get a course from a given name will auto complete the name if only the
	 * first part of a course name is input.
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
	
	/**
	 * Get the damage effect map
	 * @return The damage effect map
	 */
	public Map<String, DamageEffect> getDamageEffects() {
		return this.damageEffects;
	}
	
}
