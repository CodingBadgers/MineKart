package uk.thecodingbadgers.minekart.command;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import uk.thecodingbadgers.minekart.MineKart;
import uk.thecodingbadgers.minekart.racecourse.Racecourse;
import uk.thecodingbadgers.minekart.userstats.StatsManager;
import uk.thecodingbadgers.minekart.userstats.StatsManager.TimeResult;

public class StatsCommand {

	/**
	 * Handle the /mk times <coursename> [best] command
	 * 
	 * @param sender The thing that used the command
	 * @param args The command args
	 */
	public static void handleTimesCommand(CommandSender sender, String[] args) {
		
		if (!(sender instanceof Player)) {
			MineKart.output(sender, "This command can not be executed from the console.");
			return;
		}

		final Player player = (Player) sender;
		
		if (!player.hasPermission("minekart.stats.times")) {
			MineKart.output(player, "You do not have the required permission 'minekart.stats.times'");
			return;
		}
		
		if (args.length < 2) {
			MineKart.output(player, "Invalid command usage...");
			MineKart.output(player, " - /mk times <coursename> [best]");
			return;
		}
		
		final String name = args[1];
		Racecourse course = MineKart.getInstance().getRacecourse(name);
		if (course == null) {
			MineKart.output(sender, "Could not find a course by the name '" + name + "'");
			MineKart.output(sender, "Use the command '/mk list' to see all racecourse's.");
			return;
		}
		
		StatsManager statsManager = MineKart.getInstance().getStatsManager();

		if (args.length == 2) {
			// personal
			List<Long> times = statsManager.getPersonalTimes(player, course);
			Collections.sort(times);
			
			int toShow = Math.min(times.size(), 3);
			MineKart.output(player, "Your personal times for " + course.getName() + " are...");
			for (int index = 0; index < toShow; ++index) {
				MineKart.output(player, "[" + index + "] - " + ChatColor.YELLOW + MineKart.formatTime(times.get(index)));
			}
			return;
		}
		else if (args.length == 3) {
			// best
			List<TimeResult> times = statsManager.getCourseTimes(course);
			Collections.sort(times, new Comparator<TimeResult>() {
			    public int compare(TimeResult left, TimeResult right)  {
			    	if (left.time - right.time < 0) {
			    		return -1;
			    	} else {
			    		return 1;
			    	}
			    }
			});
			
			int toShow = Math.min(times.size(), 3);
			MineKart.output(player, "The top times for " + course.getName() + " are...");
			for (int index = 0; index < toShow; ++index) {
				TimeResult time = times.get(index);
				MineKart.output(player, "[" + index + "] - " + ChatColor.GOLD + time.player + " - " + ChatColor.YELLOW + MineKart.formatTime(time.time));
			}
			
			return;
		}
		
		
		MineKart.output(player, "Invalid command usage...");
		MineKart.output(player, " - /mk times <coursename> [best]");
	}
	
}
