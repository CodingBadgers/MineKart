package uk.thecodingbadgers.minekart.lobby;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import uk.thecodingbadgers.minekart.MineKart;
import uk.thecodingbadgers.minekart.racecourse.Racecourse;
import uk.thecodingbadgers.minekart.userstats.StatsManager;

/**
 * @author TheCodingBadgers
 * 
 *         Represents a MineKart game join sign.
 */
public class TimesSign extends LobbySign {

	/**
	 * Instantiates a new lobby sign, with the given information.
	 * 
	 * @param location the sign block for the join sign
	 * @param course the racecourse for this sign to be linked to
	 */
	public TimesSign(Block location, Racecourse course) {
		super(location, course);
	}

	/**
	 * Instantiates a new lobby sign, with no information.
	 * <p />
	 * <em>Please note, this sign will not work until you have loaded the data
	 * from a sign config file</em>
	 * 
	 * @see #load(File, FileConfiguration)
	 */
	public TimesSign() {
	}

	/**
	 * Gets the sign current desired content.
	 * 
	 * @return the sign content
	 */
	public String[] getSignContent() {
		StatsManager statsManager = MineKart.getInstance().getStatsManager();
        List<StatsManager.TimeResult> times = statsManager.getCourseTimes(course);
        Collections.sort(times, new Comparator<StatsManager.TimeResult>() {
            public int compare(StatsManager.TimeResult left, StatsManager.TimeResult right)  {
                if (left.time - right.time < 0) {
                    return -1;
                } else {
                    return 1;
                }
            }
        });

        String[] lines = new String[4];
		lines[0] = ChatColor.BOLD + course.getName();
        
        for (int index = 0; index < 3; ++index) {
            if (index >= times.size()) {
                lines[index + 1] = "---";
                continue;
            }            
            StatsManager.TimeResult time = times.get(index);
            String playerName = time.player.length() > 6 ? time.player.substring(0, 6) : time.player;
            lines[index + 1] = playerName + " " + MineKart.formatTime(time.time);
        }

		return lines;
	}
    
    /**
     * 
     * @param player 
     */
    public void onInteract(Player player) {
                        
    }
}
