package uk.thecodingbadgers.minekart.lobby;

import java.io.File;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;

import uk.thecodingbadgers.minekart.MineKart;
import uk.thecodingbadgers.minekart.race.Race;
import uk.thecodingbadgers.minekart.race.RaceState;
import uk.thecodingbadgers.minekart.racecourse.Racecourse;

/**
 * @author TheCodingBadgers
 * 
 *         Represents a MineKart game join sign.
 */
public class LobbySign {

	/** The block this sign is occupying. */
	private Block block;

	/** The racecourse associated with this join sign. */
	private Racecourse course;

	/** If this sign is enabled. */
	private boolean enabled = false;

	/** The config file for this sign */
	private File file;

	/**
	 * Instantiates a new lobby sign, with the given information.
	 * 
	 * @param location the sign block for the join sign
	 * @param course the racecourse for this sign to be linked to
	 */
	public LobbySign(Block location, Racecourse course) {
		this.block = location;
		this.course = course;

		this.enabled = true;
	}

	/**
	 * Instantiates a new lobby sign, with no information.
	 * <p />
	 * <em>Please note, this sign will not work until you have loaded the data
	 * from a sign config file</em>
	 * 
	 * @see #load(FileConfiguration)
	 */
	public LobbySign() {
	}

	/**
	 * Load the information into the sign.
	 * 
	 * @param config the config file for the sign
	 * @return true, if loaded successfully
	 */
	public boolean load(File file, FileConfiguration config) {
		this.file = file;

		World world = Bukkit.getWorld(config.getString("sign.location.world", "world"));

		if (world == null) {
			MineKart.getInstance().getLogger().warning(config.getString("sign.location.world") + " is not a valid world name.");
			return false;
		}

		Location loc = new Location(world, config.getInt("sign.location.x"), config.getInt("sign.location.y"), config.getInt("sign.location.z"));

		block = loc.getBlock();

		course = MineKart.getInstance().getRacecourse(config.getString("sign.course", "UNKNOWN"));

		if (course == null) {
			MineKart.getInstance().getLogger().warning("Lobby sign at (" + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ() + ") has a invalid racecourse");
			return false;
		}

		enabled = true;
		return true;
	}

	/**
	 * Save the information for the sign.
	 * 
	 * @param config the config file for the sign
	 */
	public void save(File file, FileConfiguration config) {
		this.file = file;

		// Location settings
		config.set("sign.location.world", block.getLocation().getWorld().getName());
		config.set("sign.location.x", block.getLocation().getBlockX());
		config.set("sign.location.y", block.getLocation().getBlockY());
		config.set("sign.location.z", block.getLocation().getBlockZ());

		// Racecourse settings
		config.set("sign.course", course.getName());
	}

	/**
	 * Update the sign with the latest information.
	 */
	public void update() {
		if (!enabled) {
			return;
		}

		Sign sign = getSign();

		if (sign == null) {
			return;
		}

		int i = 0;

		for (String string : getSignContent()) {
			sign.setLine(i, string);
			i++;
		}

		sign.update();
	}

	/**
	 * Gets the sign current desired content.
	 * 
	 * @return the sign content
	 */
	public String[] getSignContent() {
		Race race = course.getRace();

		String[] lines = new String[4];
		lines[0] = ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "[MineKart]";
		lines[1] = ChatColor.BOLD + course.getName();
		lines[2] = ChatColor.BLACK + "" + race.getJockeys().size() + "/" + course.getMultiWarp("spawn").size();
		lines[3] = getStateColor(race.getState()) + race.getState().toString();
		return lines;
	}

	/**
	 * Get the color to use for the racing state.
	 * @param state The state to get the color for
	 * @return The chatcolor to use
	 */
	private ChatColor getStateColor(RaceState state) {
		
		switch (state)
		{
		case Waiting:
			return ChatColor.GREEN;
		case Starting:
			return ChatColor.GOLD;
		case InRace:
			return ChatColor.RED;
		case Unknown:
			return ChatColor.DARK_RED;
		}
		
		return ChatColor.BLACK;
	}

	/**
	 * Gets the sign state for this join sign.
	 * 
	 * @return the sign, or null if the specified block is not a sign
	 */
	public Sign getSign() {
		BlockState state = getBlock().getState();

		if (state instanceof Sign) {
			return (Sign) state;
		}

		MineKart.getInstance().getLogger().log(Level.SEVERE, "Lobby sign is not a sign");
		enabled = false;
		return null;
	}


	/**
	 * Gets the block this join sign is occupying.
	 * 
	 * @return the block this join sign is occupying
	 */
	public Block getBlock() {
		return block;
	}

	/**
	 * Gets the course that this join sign is linked to.
	 * 
	 * @return the course that this join sign is linked to
	 */
	public Racecourse getCourse() {
		return course;
	}

	/**
	 * Gets whether this sign is currently enabled, no logic will be performed
	 * on this sign until it is enabled.
	 * 
	 * @return true, if the sign is enabled, false othewise
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * Destroy this sign, removing all data on this sign in memory and on
	 * disk.
	 */
	public void destroy() {
		block = null;
		course = null;
		enabled = false;

		if (!file.delete()) {
			file.deleteOnExit();
		}

		file = null;
	}
}
