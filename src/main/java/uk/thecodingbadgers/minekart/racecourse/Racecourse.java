package uk.thecodingbadgers.minekart.racecourse;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;

import uk.thecodingbadgers.minekart.MineKart;

/**
 * @author TheCodingBadgers
 *
 * The base racecourse class, used to define a racecourse
 *
 */
public abstract class Racecourse {
	
	/** The world that the racecouse resides in */
	protected World world = null;
	
	/** The bounds of the racecourse */
	protected Region bounds = null;
	
	/** The name of the racecourse */
	protected String name = null;
	
	/** The spawn points for the jockeys on race start */
	protected List<Location> spawnPoints = null;
	
	/** The lobby spawn location */
	protected Location lobbySpawn = null;
	
	/** The spectator spawn location */
	protected Location spectateSpawn = null;

	/**
	 * Setup the racecourse. Setting up the bounds of the arena based on player world edit seleciton.
	 * @param player The player who is setting up the course
	 * @return True if the location is within the course bounds, false otherwise.
	 */
	public boolean setup(Player player, String name) {
		
		WorldEditPlugin worldEdit = MineKart.getInstance().getWorldEditPlugin();
		Selection seleciton = worldEdit.getSelection(player);
		if (seleciton == null) {
			MineKart.output(player, "Please make a world edit selection covering the bounds of the racecourse...");
			return false;
		}
		
		// Set the arena bounds from the selection
		world = seleciton.getWorld();
		try {
			bounds = seleciton.getRegionSelector().getRegion();
		} catch (IncompleteRegionException e) {
			MineKart.output(player, "");
			return false;
		}
		
		this.name = name;
		this.spawnPoints = new ArrayList<Location>();

		return true;
	}
	
	/**
	 * Check to see if a given location is within the course bounds.
	 * @param location The location to check
	 * @return True if the location is within the course bounds, false otherwise.
	 */
	public boolean isInCourseBounds(Location location) {
		
		// Is the location in the same world
		if (!location.getWorld().equals(this.world))
			return false;
		
		// Create a world edit vector and test against the course bounds
		com.sk89q.worldedit.Vector vec = new com.sk89q.worldedit.Vector();
		vec.setX(location.getX());
		vec.setY(location.getY());
		vec.setZ(location.getZ());
		
		return bounds.contains(vec);
	}
	
	/**
	 * Load the racecourse from file.
	 */	
	public void load(FileConfiguration file) {
		
		// Course name
		this.name = file.getString("racecourse.name");
		
		// Course bounds
		this.world = Bukkit.getWorld(file.getString("racecourse.world"));
		this.bounds = loadRegion(file, "racecourse.bounds");
		
		// Course spawns
		this.spawnPoints = new ArrayList<Location>();
		int noofSpawnPoints = file.getInt("racecourse.spawn.count");
		
		for (int spawnIndex = 0; spawnIndex < noofSpawnPoints; ++spawnIndex) {			
			this.spawnPoints.add(loadLocation(file, "racecourse.spawn." + spawnIndex));
		}
		
		// Spawn points
		this.lobbySpawn = loadLocation(file, "racecourse.lobbyspawn");
		this.spectateSpawn = loadLocation(file, "racecourse.spectatespawn");
		
	}
	
	/**
	 * Save the racecourse to file.
	 */	
	public void save(FileConfiguration file) {
		
		// Course name
		file.set("racecourse.name", this.name);
		
		// Course bounds
		file.set("racecourse.world", this.world.getName());
		saveRegion(file, "racecourse.bounds", this.bounds);

		// Course spawns
		file.set("racecourse.spawn.count", this.spawnPoints.size());
		int spawnIndex = 0;
		for (Location spawn : this.spawnPoints) {
			saveLocation(file, "racecourse.spawn." + spawnIndex, spawn);
			spawnIndex++;
		}
		
		// Spawn points
		saveLocation(file, "racecourse.lobbyspawn", this.lobbySpawn);
		saveLocation(file, "racecourse.spectatespawn", this.spectateSpawn);
		
	}
	
	/**
	 * Save a given location to a file configuration
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
	 * @param file The file to save too
	 * @param path The path in the file config
	 * @return The loaded location.
	 */
	protected Location loadLocation(FileConfiguration file, String path) {
		Double x = file.getDouble(path + ".x");
		Double y = file.getDouble(path + ".y");
		Double z = file.getDouble(path + ".z");
		float pitch = (float)file.getDouble(path + ".pitch");
		float yaw = (float)file.getDouble(path + ".yaw");
		
		return new Location(this.world, x, y, z, yaw, pitch);
	}
	
	/**
	 * Save a given region to a file configuration
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
	 * @param file The file to save too
	 * @param path The path in the file config
	 * @return The loaded region.
	 */
	protected Region loadRegion(FileConfiguration file, String path) {
		Double minX = file.getDouble(path + ".min.x");
		Double minY = file.getDouble(path + ".min.y");
		Double minZ = file.getDouble(path + ".min.z");
		Double maxX = file.getDouble(path + ".max.x");
		Double maxY = file.getDouble(path + ".max.y");
		Double maxZ = file.getDouble(path + ".max.z");
		
		return new CuboidRegion(
				new com.sk89q.worldedit.Vector(minX, minY, minZ),
				new com.sk89q.worldedit.Vector(maxX, maxY, maxZ)
				);	
	}
	
	/**
	 * Output the remaining requirements to complete this arena
	 * @param sender The sender to receive the output information
	 * @return True if all requirements have been met
	 */
	public boolean outputRequirements(CommandSender sender) {
		
		boolean fullySetup = true;
		
		if (this.spawnPoints.size() < 2) {
			MineKart.output(sender, " - Add spawn points (minimum of 2 required) [/mk addspawn <coursename>]");
			fullySetup = false;
		}
		
		if (this.lobbySpawn == null) {
			MineKart.output(sender, " - Add a lobby spawn point [/mk setlobby <coursename>]");
			fullySetup = false;
		}
		
		if (this.spectateSpawn == null) {
			MineKart.output(sender, " - Add a spectator spawn point [/mk setspectate <coursename>]");
			fullySetup = false;
		}
		
		return fullySetup;
	}

}
