package uk.thecodingbadgers.minekart.command;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import uk.thecodingbadgers.minekart.MineKart;
import uk.thecodingbadgers.minekart.racecourse.Racecourse;

public class CourseCommand {

	/**
	 * Handle the /mk course <coursename> <enable|disable> command
	 * @param sender The thing that used the command
	 * @param args The command args
	 */
	public static void handleEnableCommand(CommandSender sender, String[] args) {
		
		if (!sender.hasPermission("minekart.course.enable")) {
			MineKart.output(sender, "You do not have the required permission 'minekart.course.enable'");
			return;
		}
		
		if (args.length == 3) {
			
			final String coursename = args[1];
			Racecourse course = MineKart.getInstance().getRacecourse(coursename);
			if (course == null) {
				MineKart.output(sender, "Could not find a racecourse with the name '" + coursename + "'.");
				MineKart.output(sender, "Use the command '/mk list' to see all racecourse's.");
				return;
			}
			
			boolean enabled;
			if (args[2].equalsIgnoreCase("enable")) {
				enabled = true;
			}
			else if (args[2].equalsIgnoreCase("disable")) {
				enabled = false;
			}
			else {
				MineKart.output(sender, "Invalid command usage...");
				MineKart.output(sender, " - /mk course <coursename> <enable|disable>");
				return;
			}
			
			course.setEnabled(enabled);
			MineKart.output(sender, "The course '" + coursename + "' has been " + ChatColor.YELLOW + (enabled ? "Enabled" : "Disabled") + ChatColor.WHITE + ".");
			return;
		}
		
		MineKart.output(sender, "Invalid command usage...");
		MineKart.output(sender, " - /mk course <coursename> <enable|disable>");	
		
	}

}
