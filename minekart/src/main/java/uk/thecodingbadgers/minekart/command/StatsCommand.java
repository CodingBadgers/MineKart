package uk.thecodingbadgers.minekart.command;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import uk.thecodingbadgers.minekart.MineKart;
import uk.thecodingbadgers.minekart.lang.Lang;
import uk.thecodingbadgers.minekart.lang.LangUtils;
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
			LangUtils.sendMessage(sender, "command.error.console");
			return;
		}

		final Player player = (Player) sender;
		
		if (!player.hasPermission("minekart.stats.times")) {
			LangUtils.sendMessage(sender, "command.error.permission", "minekart.stats.times");
			return;
		}
		
		if (args.length < 2) {
			LangUtils.sendMessage(sender, "command.error.usage");
			LangUtils.sendMessage(sender, "command.times.usage");
			return;
		}
		
		final String name = args[1];
		Racecourse course = MineKart.getInstance().getRacecourse(name);
		if (course == null) {
			LangUtils.sendMessage(sender, "command.error.notfound");
			LangUtils.sendMessage(sender, "command.error.list");
			return;
		}
		
		StatsManager statsManager = MineKart.getInstance().getStatsManager();

		if (args.length == 2) {
			// personal
			List<Long> times = statsManager.getPersonalTimes(player, course);
			Collections.sort(times);
			
			int toShow = Math.min(times.size(), 3);

			LangUtils.sendMessage(sender, "command.times.personal", course.getName());
			for (int index = 0; index < toShow; ++index) {
				LangUtils.sendMessage(sender, "command.times.entry", index, MineKart.formatTime(times.get(index)));
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
			LangUtils.sendMessage(sender, "command.times.personal", course.getName());
			for (int index = 0; index < toShow; ++index) {
				TimeResult time = times.get(index);
				LangUtils.sendMessage(sender, "command.times.entry", index, MineKart.formatTime(time.time));
			}
			
			return;
		}
		
		LangUtils.sendMessage(sender, "command.error.usage");
		LangUtils.sendMessage(sender, "command.times.usage");
	}
	
}
