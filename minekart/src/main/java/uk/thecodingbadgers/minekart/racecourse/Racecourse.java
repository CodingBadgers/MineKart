package uk.thecodingbadgers.minekart.racecourse;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalWorld;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;

import uk.thecodingbadgers.minekart.MineKart;
import uk.thecodingbadgers.minekart.jockey.Jockey;
import uk.thecodingbadgers.minekart.lang.LangUtils;
import uk.thecodingbadgers.minekart.lobby.LobbySignManager;
import uk.thecodingbadgers.minekart.mount.MountType;
import uk.thecodingbadgers.minekart.mount.MountTypeData;
import uk.thecodingbadgers.minekart.powerup.PowerupEntity;
import uk.thecodingbadgers.minekart.race.Race;
import uk.thecodingbadgers.minekart.race.RaceSinglePlayer;
import uk.thecodingbadgers.minekart.race.RaceState;
import uk.thecodingbadgers.minekart.util.FireworkFactory;
import uk.thecodingbadgers.minekart.util.MinecraftTime;
import uk.thecodingbadgers.minekart.version.NmsHandler;
import uk.thecodingbadgers.minekart.world.BlockChangeDelagator;
import uk.thecodingbadgers.minekart.world.BlockDelagatorFactory;

/**
 * @author TheCodingBadgers
 * 
 *         The base racecourse class, used to define a racecourse
 * 
 */
public abstract class Racecourse {

	protected World world = null;
	protected Region bounds = null;
	protected String name = null;
	protected String type = null;
	
	protected Map<String, Integer> minimums = null;
	protected Map<String, List<Location>> multiPoints = null;
	protected Map<String, Location> singlePoints = null;
	protected Map<String, ItemStack[]> pointMappings = null;
	
	protected File fileConfiguration = null;
	protected Race race = null;
	protected MountType mountType = MountType.HORSE;
	protected boolean enabled = true;
	protected List<PowerupEntity> powerupItems = null;
	protected List<String> powerupBlacklist = null;
	protected Material readyblock;
	protected int minimumNoofPlayers = 2;
	protected int powerupCooldown = 1000;
	protected MountTypeData mountTypeData;

	/**
	 * Class constructor
	 */
	public Racecourse() {

		this.multiPoints = new HashMap<String, List<Location>>();
		this.singlePoints = new HashMap<String, Location>();
		this.pointMappings = new HashMap<String, ItemStack[]>();
		this.powerupItems = new ArrayList<PowerupEntity>();
		this.powerupBlacklist = new ArrayList<String>();

		registerWarp(Bukkit.getConsoleSender(), "spawn", "add", 2, new ItemStack(Material.DIAMOND_BLOCK));
		registerWarp(Bukkit.getConsoleSender(), "powerup", "add", 0, new ItemStack(Material.GOLD_BLOCK));
		registerWarp(Bukkit.getConsoleSender(), "lobby", "set", 1, new ItemStack(Material.IRON_BLOCK));
		registerWarp(Bukkit.getConsoleSender(), "spectate", "set", 1, new ItemStack(Material.LAPIS_BLOCK));
	}

	/**
	 * Setup the racecourse. Setting up the bounds of the arena based on
	 * player world edit seleciton.
	 * 
	 * @param player The player who is setting up the course
	 * @return True if the location is within the course bounds, false
	 *         otherwise.
	 */
	public boolean setup(Player player, String name) {

		WorldEditPlugin worldEdit = MineKart.getInstance().getWorldEditPlugin();
		Selection selection = worldEdit.getSelection(player);
		if (selection == null) {
            LangUtils.sendMessage(player, "course.create.error.worldedit");
			return false;
		}

		// Set the arena bounds from the selection
		world = selection.getWorld();
		try {
			bounds = selection.getRegionSelector().getRegion().clone();
		} catch (IncompleteRegionException e) {
            LangUtils.sendMessage(player, "course.create.error.region");
			return false;
		}

		this.name = name;
		this.readyblock = Material.IRON_BLOCK;
        this.mountType = MountType.HORSE;
		this.mountTypeData = MineKart.getInstance().getMountDataRegistry().getMountData(mountType);

		this.fileConfiguration = new File(MineKart.getRacecourseFolder() + File.separator + this.name + "." + this.type + ".yml");
		if (!this.fileConfiguration.exists()) {
			try {
				if (!this.fileConfiguration.createNewFile()) {
                    LangUtils.sendMessage(player, "course.create.error.config", this.name);
                    LangUtils.sendMessage(player, "course.create.error.config.loc", this.fileConfiguration.getAbsolutePath());
					return false;
				}
			} catch (Exception ex) {
                LangUtils.sendMessage(player, "course.create.error.config", this.name);
                LangUtils.sendMessage(player, "course.create.error.config.loc", this.fileConfiguration.getAbsolutePath());
				return false;
			}
		}

		this.race = new RaceSinglePlayer();
		this.race.setCourse(this);

		return true;
	}

	/**
	 * Delete this racecourse
	 */
	public boolean delete() {
		return this.fileConfiguration.delete();
	}

	/**
	 * Check to see if a given location is within the course bounds.
	 * 
	 * @param location The location to check
	 * @return True if the location is within the course bounds, false
	 *         otherwise.
	 */
	public boolean isInCourseBounds(Location location) {

		// Is the location in the same world
		if (!location.getWorld().equals(this.world)) {
            return false;
        }

		// Create a world edit vector and test against the course bounds
		com.sk89q.worldedit.Vector vec = new com.sk89q.worldedit.Vector(location.getX(), location.getY(), location.getZ());

		return this.bounds.contains(vec);
	}

	/**
	 * Load the racecourse from file.
	 */
	@SuppressWarnings("deprecation")
	public void load(File configfile) {

		FileConfiguration file = YamlConfiguration.loadConfiguration(configfile);
		this.fileConfiguration = configfile;

		// Course name
		this.name = file.getString("racecourse.name");

		// Enabled State
		this.enabled = file.getBoolean("racecourse.enabled", this.enabled);

		// Course bounds
		this.world = Bukkit.getWorld(file.getString("racecourse.world"));
		this.bounds = loadRegion(file, "racecourse.bounds");

		// Mount settings
		final String loadedMount = file.getString("mount.type", "EntityHorse");
		this.mountType = loadedMount.equalsIgnoreCase("none") ? MountType.HORSE : MountType.fromEntityId(loadedMount);
		this.mountTypeData = MineKart.getInstance().getMountDataRegistry().getMountData(mountType);
		ConfigurationSection section = file.getConfigurationSection("mount.data");

		if (section != null) {
			this.mountTypeData.loadData(section);
		}

		// Powerup settings
		List<String> blacklistPowerup = file.getStringList("powerup.blacklist");

		this.powerupBlacklist = new ArrayList<String>();
		for (String powerup : blacklistPowerup) {
			this.powerupBlacklist.add(powerup.toLowerCase());
		}

		// Lobby settings
		this.readyblock = Material.getMaterial(file.getString("lobby.readyblock", "IRON_BLOCK"));
		this.minimumNoofPlayers = file.getInt("racecourse.minimumJockeys", 2);
		this.powerupCooldown = file.getInt("racecourse.powerupCooldown", 1000);

		// Single point locations
		int noofSinglePoints = file.getInt("racecourse.singlepoint.count");
		for (int pointIndex = 0; pointIndex < noofSinglePoints; ++pointIndex) {
			final String path = "racecourse.singlepoint." + pointIndex;
			final String name = file.getString(path + ".name");
			final Location location = loadLocation(file, path + ".location");
			this.singlePoints.put(name, location);
		}

		// Multi-point locations
		int noofMultiPoints = file.getInt("racecourse.multipoint.count");
		for (int pointIndex = 0; pointIndex < noofMultiPoints; ++pointIndex) {
			final String path = "racecourse.multipoint." + pointIndex;
			List<Location> locations = new ArrayList<Location>();

			final String name = file.getString(path + ".name");
			final int noofLocations = file.getInt(path + ".count");
			for (int locationIndex = 0; locationIndex < noofLocations; ++locationIndex) {
				locations.add(loadLocation(file, path + ".location." + locationIndex));
			}

			this.multiPoints.put(name, locations);
		}

		this.race = new RaceSinglePlayer();
		this.race.setCourse(this);
	}

	/**
	 * Save the racecourse to file.
	 */
	@SuppressWarnings("deprecation")
	public void save() {

		FileConfiguration file = YamlConfiguration.loadConfiguration(this.fileConfiguration);

		// Course name
		file.set("racecourse.name", this.name);

		// Enabled State
		file.set("racecourse.enabled", this.enabled);

		// Course bounds
		file.set("racecourse.world", this.world.getName());
		saveRegion(file, "racecourse.bounds", this.bounds);

		// Mount settings
		file.set("mount.type", this.mountType == MountType.HORSE ? "none" : this.mountType.getBukkitMapping().getName());

		ConfigurationSection data = file.getConfigurationSection("mount.data");
		if (data == null) {
			data = file.createSection("mount.data");
		}
		file.set("mount.data", this.mountTypeData.getSaveData(data));

		// Powerup settings;
		file.set("powerup.blacklist", this.powerupBlacklist);

		// Lobby settings
		file.set("lobby.readyblock", this.readyblock.name());
		file.set("racecourse.minimumJockeys", this.minimumNoofPlayers);
		file.set("racecourse.powerupCooldown", this.powerupCooldown);

		// Single point locations
		file.set("racecourse.singlepoint.count", this.singlePoints.size());
		int pointIndex = 0;
		for (Entry<String, Location> point : this.singlePoints.entrySet()) {

			if (point.getValue() == null) {
				continue;
			}

			final String path = "racecourse.singlepoint." + pointIndex;
			file.set(path + ".name", point.getKey());
			saveLocation(file, path + ".location", point.getValue());
			pointIndex++;
		}

		// Multi-point locations
		file.set("racecourse.multipoint.count", this.multiPoints.size());
		pointIndex = 0;
		for (Entry<String, List<Location>> point : this.multiPoints.entrySet()) {
			final String path = "racecourse.multipoint." + pointIndex;
			List<Location> locations = point.getValue();

			if (locations == null || locations.isEmpty()) {
				continue;
			}

			file.set(path + ".name", point.getKey());
			file.set(path + ".count", locations.size());
			int locationIndex = 0;
			for (Location location : locations) {
				saveLocation(file, path + ".location." + locationIndex, location);
				locationIndex++;
			}
			pointIndex++;
		}

		try {
			file.save(this.fileConfiguration);
		} catch (Exception ex) {
            ex.printStackTrace();
		}
	}

	/**
	 * Save a given location to a file configuration
	 * 
	 * @param file The file to save too
	 * @param path The path in the file config
	 * @param location The location to save.
	 */
	protected void saveLocation(FileConfiguration file, String path, Location location) {
		file.set(path + ".x", location.getX());
		file.set(path + ".y", location.getY());
		file.set(path + ".z", location.getZ());
		file.set(path + ".pitch", location.getPitch());
		file.set(path + ".yaw", location.getYaw());
	}

	/**
	 * Load a given location from a file configuration
	 * 
	 * @param file The file to save too
	 * @param path The path in the file config
	 * @return The loaded location.
	 */
	protected Location loadLocation(FileConfiguration file, String path) {
		Double x = file.getDouble(path + ".x");
		Double y = file.getDouble(path + ".y");
		Double z = file.getDouble(path + ".z");
		float pitch = (float) file.getDouble(path + ".pitch");
		float yaw = (float) file.getDouble(path + ".yaw");

		return new Location(this.world, x, y, z, yaw, pitch);
	}

	/**
	 * Save a given region to a file configuration
	 * 
	 * @param file The file to save too
	 * @param path The path in the file config
	 * @param region The region to save.
	 */
	protected void saveRegion(FileConfiguration file, String path, Region region) {
		file.set(path + ".min.x", region.getMinimumPoint().getX());
		file.set(path + ".min.y", region.getMinimumPoint().getY());
		file.set(path + ".min.z", region.getMinimumPoint().getZ());
		file.set(path + ".max.x", region.getMaximumPoint().getX());
		file.set(path + ".max.y", region.getMaximumPoint().getY());
		file.set(path + ".max.z", region.getMaximumPoint().getZ());
	}

	/**
	 * Load a given region from a file configuration
	 * 
	 * @param file The file to save too
	 * @param path The path in the file config
	 * @return The loaded region.
	 */
	protected Region loadRegion(FileConfiguration file, String path) {
		LocalWorld world = BukkitUtil.getLocalWorld(this.world);
		Double minX = file.getDouble(path + ".min.x");
		Double minY = file.getDouble(path + ".min.y");
		Double minZ = file.getDouble(path + ".min.z");
		Double maxX = file.getDouble(path + ".max.x");
		Double maxY = file.getDouble(path + ".max.y");
		Double maxZ = file.getDouble(path + ".max.z");

		return new CuboidRegion(world, new com.sk89q.worldedit.Vector(minX, minY, minZ), new com.sk89q.worldedit.Vector(maxX, maxY, maxZ));
	}

	/**
	 * Output the remaining requirements to complete this arena
	 * 
	 * @param sender The sender to receive the output information
	 * @return True if all requirements have been met
	 */
	public boolean outputRequirements(CommandSender sender) {

		boolean fullySetup = true;

		// single points
		for (Entry<String, Location> point : this.singlePoints.entrySet()) {
			if (point.getValue() == null) {
                LangUtils.sendMessage(sender, "course.requirements.single", point.getKey(), this.getName());
				fullySetup = false;
			}
		}

		// multi-points
		for (Entry<String, List<Location>> point : this.multiPoints.entrySet()) {
			if (point.getValue() == null || point.getValue().size() < this.minimums.get(point.getValue())) {
                LangUtils.sendMessage(sender, "course.requirements.multi", point.getKey(), this.getName(), this.minimums.get(point.getValue()) - point.getValue().size());
				fullySetup = false;
			}
		}

		return fullySetup;
	}

	/**
	 * Output all information about this racecourse
	 * 
	 * @param sender The thing to tell the information
	 */
	public void outputInformation(CommandSender sender) {

        LangUtils.sendMessage(sender, "course.info.name", this.name);
        LangUtils.sendMessage(sender, "course.info.world", this.world.getName());
        LangUtils.sendMessage(sender, "course.info.bounds", this.bounds.toString());
        LangUtils.sendMessage(sender, "course.info.separator", this.bounds.toString());

		for (Entry<String, Location> point : this.singlePoints.entrySet()) {
			if (point.getValue() != null) {
                LangUtils.sendMessage(sender, "course.info.point.single", point.getValue(), point.getValue().getBlockX(), point.getValue().getBlockY(), point.getValue().getBlockZ());
			}
		}
        LangUtils.sendMessage(sender, "course.info.separator", this.bounds.toString());

		for (Entry<String, List<Location>> point : this.multiPoints.entrySet()) {
			if (point.getValue() != null && !point.getValue().isEmpty()) {
                LangUtils.sendMessage(sender, "course.info.point.multi", point.getValue());
				int id = 0;
				for (Location location : point.getValue()) {
                    LangUtils.sendMessage(sender, "course.info.point.multi.entry", id, location.getBlockX(), location.getBlockY(), location.getBlockZ());
					id++;
				}
			}
		}
        LangUtils.sendMessage(sender, "course.info.separator", this.bounds.toString());

	}

	/**
	 * Register a warp type
	 * 
	 * @param player The player registering the warp
	 * @param name The name of the warp to register
	 * @param type The type of warp to register, set or add.
	 * @param materials the materials to set this warp to in the show warp
	 *            command
	 */
	public void registerWarp(CommandSender player, String name, String type, int min, ItemStack... materials) {

		this.pointMappings.put(name, materials);

		if (type.equalsIgnoreCase("set")) {
			if (this.singlePoints.containsKey(name)) {
                return;
            }

			this.singlePoints.put(name, null);
		} else if (type.equalsIgnoreCase("add")) {
			if (this.multiPoints.containsKey(name)) {
                return;
            }

			this.minimums.put(name,  min);
			this.multiPoints.put(name, new ArrayList<Location>());
		} else {
            LangUtils.sendMessage(player, "course.warp.unknown");
		}

	}

	/**
	 * Set a single point warp
	 * 
	 * @param player The player setting the warp
	 * @param warpname The name of the warp to set
	 */
	public void setWarp(Player player, String warpname) {

		if (!this.singlePoints.containsKey(warpname)) {
            LangUtils.sendMessage(player, "course.warp.single.unknown", warpname);
			return;
		}

		this.singlePoints.remove(warpname);
		this.singlePoints.put(warpname, player.getLocation());
        LangUtils.sendMessage(player, "course.warp.single.set", warpname);
	}

	/**
	 * Add a multi-point warp
	 * 
	 * @param player The player adding the warp
	 * @param warpname The name of the warp to add to
	 */
	public void addWarp(Player player, String warpname) {

		if (!this.multiPoints.containsKey(warpname)) {
            LangUtils.sendMessage(player, "course.warp.multi.unknown", warpname);
			return;
		}

		List<Location> locations = this.multiPoints.get(warpname);
		this.multiPoints.remove(warpname);
		locations.add(player.getLocation());
		this.multiPoints.put(warpname, locations);

        LangUtils.sendMessage(player, "course.warp.multi.add", this.getName());
        LangUtils.sendMessage(player, "course.warp.multi.update", warpname, locations.size());
	}

	/**
	 * Get the name of the racecourse
	 * 
	 * @return The name of the racecourse
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Get the race which uses the racecourse
	 * 
	 * @return The race instance
	 */
	public Race getRace() {
		return this.race;
	}

	/**
	 * Get a warp by its name
	 * 
	 * @param warpname The name of the warp to find
	 * @return The location of the given warp, or null if a warp wasn't found
	 */
	public Location getWarp(String warpname) {

		if (this.singlePoints.containsKey(warpname)) {
			return this.singlePoints.get(warpname);
		}

		return null;
	}

	/**
	 * Get a multi warp by its name
	 * 
	 * @param warpname The name of the warp to find
	 * @return The list of locations of the given warp, or null if a warp
	 *         wasn't found
	 */
	public List<Location> getMultiWarp(String warpname) {

		if (this.multiPoints.containsKey(warpname)) {
			return this.multiPoints.get(warpname);
		}

		return null;
	}

	/**
	 * Get the bounds of the racecourse
	 * 
	 * @return The bounds of the racecourse
	 */
	public Object getBounds() {
		return this.bounds;
	}

	/**
	 * Called when a jockey moves
	 * 
	 * @param jockey The jockey who moved
	 * @param race The race the jockeys are in
	 */
	public boolean onJockeyMove(Jockey jockey, Race race) {

		if (race.getState() != RaceState.InRace) {
            return false;
        }

		if (!this.bounds.contains(jockey.getWorldEditLocation())) {
			jockey.respawn();
			return false;
		}

		return true;
	}

	/**
	 * Called when a race starts
	 * 
	 * @param race The race which is starting
	 */
	public void onRaceStart(Race race) {

		List<Location> powerups = this.multiPoints.get("powerup");
		for (Location location : powerups) {
			Location spawnLocation = new Location(location.getWorld(), location.getX(), location.getY() + 1.0, location.getZ());
			spawnPowerup(spawnLocation);
		}
	}

	/**
	 * 
	 * @param location
	 */
	private void spawnPowerup(Location location) {

		ItemStack powerup = new ItemStack(Material.CHEST); // TODO: Make configrable
		ItemMeta meta = powerup.getItemMeta();
		meta.setDisplayName("Powerup " + (new Random()).nextInt());
		powerup.setItemMeta(meta);
		powerup.addUnsafeEnchantment(Enchantment.PROTECTION_FIRE, 0);

		PowerupEntity entity = NmsHandler.getNmsHandler().newPowerup(location, powerup);
		entity.spawn();
		this.powerupItems.add(entity);

		location.getWorld().playSound(location, Sound.FIREWORK_TWINKLE, 1.0f, 1.0f);

		FireworkFactory.SpawnFireworkExplosion(location, Type.BALL, Color.RED);
	}

	/**
	 * Called when a race ends
	 * 
	 * @param race The race which is ending
	 */
	public void onRaceEnd(Race race) {
		
		for (PowerupEntity powerup : this.powerupItems) {
			powerup.remove();
		}
		
		for (Entity entity : this.world.getEntities()) {
			if (entity instanceof Player) {
                continue;
            }
			
			final Location location = entity.getLocation();
			final Vector entityLocation = new Vector(location.getBlockX(), location.getBlockY(), location.getBlockZ());
			if (!this.bounds.contains(entityLocation)) {
                continue;
            }
			
			if (CitizensAPI.getNPCRegistry().isNPC(entity)) {
				NPC npc = CitizensAPI.getNPCRegistry().getNPC(entity);
				npc.destroy();
			}
			else {
				entity.remove();
			}
		}

	}

	/**
	 * Get the mount type this race course uses
	 * 
	 * @return The EntityType that this course uses as a mount (Unknown means
	 *         none)
	 */
	public MountType getMountType() {
		return this.mountType;
	}

	/**
	 * Set the mount type this race course uses (Unknown means none)
	 * 
	 * @param mountType The EntityType that this course should use as a mount
	 */
	public void setMountType(MountType mountType) {
		this.mountType = mountType;
		this.save();
	}

	/**
	 * Set the enabled state of the course
	 * 
	 * @param enabled True to enable the course, False to disable
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
		LobbySignManager.updateSigns();
		this.save();
	}

	/**
	 * Get the enabled state of the race course
	 * 
	 * @return True if enabled, false if disabled.
	 */
	public boolean isEnabled() {
		return this.enabled;
	}

	/**
	 * Remove a powerup at a given location.
	 * 
	 * @param location The location of the powerup
	 */
	public void removePowerup(Location location) {
		this.powerupItems.remove(location); // TODO check this actually works, doesn't seem like it will
		location.getWorld().playSound(location, Sound.VILLAGER_YES, 1.0f, 1.0f);

		final Location spawnLocation = new Location(location.getWorld(), location.getX(), location.getY(), location.getZ());

		Bukkit.getScheduler().runTaskLater(MineKart.getInstance(), new Runnable() {

			@Override
			public void run() {
				spawnPowerup(spawnLocation);
			}

		}, MinecraftTime.fromSeconds(5));
	}

	/**
	 * Gets the block the jockeys have to hit to "ready up"
	 * 
	 * @return The block the jockeys have to hit to "ready up"
	 */
	public Material getReadyBlock() {
		return this.readyblock;
	}

	/**
	 * Get the minimum number of players need to for a race to take place
	 * 
	 * @return The minimum number of players
	 */
	public int getMinimumPlayers() {
		return this.minimumNoofPlayers;
	}

	/**
	 * Gets the time between players being able to pickup powerups, in
	 * milliseconds
	 * 
	 * @return the time between players being able to pickup powerups, in
	 *         milliseconds
	 */
	public long getPowerupCooldown() {
		return powerupCooldown;
	}

	/**
	 * 
	 * @return
	 */
	public List<String> getPowerupBlackList() {
		return this.powerupBlacklist;
	}

	/**
	 * Gets the custom data for the mount type for this racecourse
	 * 
	 * @return the custom mount type data
	 */
	public MountTypeData getMountData() {
		return this.mountTypeData;
	}

	/**
	 * Shows the player specified all the warps for this racecourse
	 * 
	 * @param player the player to show the warps
	 * @param warptype the warp type to show
	 */
	public boolean showWarps(final Player player, String warptype) {
		return showWarps(BlockDelagatorFactory.createChangeDelagator("fake", player), warptype);
	}

	protected boolean showWarps(BlockChangeDelagator delagator, String warptype) {
		final Map<Location, BlockState> changes = new HashMap<Location, BlockState>();

		ItemStack[] materials = this.pointMappings.get(warptype);

		if (materials == null) {
			materials = new ItemStack[] { new ItemStack(Material.WOOL) };
		}

		if (this.singlePoints.containsKey(warptype)) {
			Location loc = this.singlePoints.get(warptype);
			changes.put(loc, loc.getBlock().getState());

			ItemStack material = materials[0];
			delagator.setBlock(loc, material.getType(), material.getData());
		} else if (this.multiPoints.containsKey(warptype)) {
			for (Location loc : this.multiPoints.get(warptype)) {
				changes.put(loc, loc.getBlock().getState());

				ItemStack material = materials[0];
				delagator.setBlock(loc, material.getType(), material.getData());
			}
		} else {
			return false;
		}

		delagator.delayResetChanges(5 * 20L);
		return true;
	}

	protected Location toBukkit(BlockVector block) {
		return new Location(world, block.getBlockX(), block.getBlockY(), block.getBlockZ());
	}

	/**
	 * Remove a warp with a given name and id
	 * @param player The player removing the warp
	 * @param warpName The name of the warp
	 * @param id THe id of the warp, or -1 to remove last warp created
	 * @return
	 */
	public boolean removeWarp(Player player, String warpName, int id) {
		
		if (this.singlePoints.containsKey(warpName)) {
			this.singlePoints.remove(warpName);			
			return true;
		}
		
		if (this.multiPoints.containsKey(warpName)) {
			List<Location> warps = this.multiPoints.get(warpName);
			id = id == -1 ? warps.size() - 1 : id;
			if (id < 0 || id >= warps.size()) {
                LangUtils.sendMessage(player, "course.warp.delete.error", warpName, id);
				return false;
			}
			warps.remove(id);			
			return true;
		}
		
		return false;
	}
}
