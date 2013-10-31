package uk.thecodingbadgers.minekart.command;

import java.util.Map;

import org.bukkit.command.CommandSender;

import uk.thecodingbadgers.minekart.MineKart;
import uk.thecodingbadgers.minekart.racecourse.Racecourse;

public class HelperCommand {

	/**
	 * Handle the /mk list command
	 * 
	 * @param sender The thing that used the command
	 * @param args The command args
	 */
	public static void handleListCommand(CommandSender sender, String[] args) {

		if (!sender.hasPermission("minekart.course.list")) {
			MineKart.output(sender, "You do not have the required permission 'minekart.course.list'");
			return;
		}

		Map<String, Racecourse> courses = MineKart.getInstance().getAllRacecourses();

		if (courses.isEmpty()) {
			MineKart.output(sender, "No racecourses exists.");
		} else {
			MineKart.output(sender, "The following racecourses exists...");
			for (Racecourse course : courses.values()) {
				MineKart.output(sender, " - " + course.getName());
			}
		}

	}

	/**
	 * Handle the /mk info <coursename> command
	 * 
	 * @param sender The thing that used the command
	 * @param args The command args
	 */
	public static void handleInfoCommand(CommandSender sender, String[] args) {

		if (!sender.hasPermission("minekart.course.info")) {
			MineKart.output(sender, "You do not have the required permission 'minekart.course.info'");
			return;
		}

		if (args.length == 2) {
			final String coursename = args[1];
			Racecourse course = MineKart.getInstance().getRacecourse(coursename);
			if (course == null) {
				MineKart.output(sender, "Could not find a racecourse with the name '" + coursename + "'.");
				return;
			}

			course.outputInformation(sender);
			return;
		}

		MineKart.output(sender, "Invalid command usage...");
		MineKart.output(sender, " - /mk info <coursename>");
	}

}
