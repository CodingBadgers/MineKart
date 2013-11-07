package uk.thecodingbadgers.minekart.command;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import uk.thecodingbadgers.minekart.MineKart;
import uk.thecodingbadgers.minekart.racecourse.RacecourceType;
import uk.thecodingbadgers.minekart.racecourse.Racecourse;

public class CourseCommand {

	/**
	 * Handle the /mk course <coursename> create <type> command
	 * 
	 * @param sender The thing that used the command
	 * @param args The command args
	 */
	public static void handleCreateCommand(CommandSender sender, String[] args) {

		if (!(sender instanceof Player)) {
			MineKart.output(sender, "This command can not be executed from the console.");
			return;
		}

		final Player player = (Player) sender;

		if (!player.hasPermission("minekart.create.course")) {
			MineKart.output(player, "You do not have the required permission 'minekart.create.course'");
			return;
		}

		// course <coursename> create <type>
		if (args.length == 4) {

			final String name = args[1];
			Racecourse course = MineKart.getInstance().getRacecourse(name);
			if (course != null) {
				MineKart.output(sender, "A course with the name '" + name + "' already exists...");
				MineKart.output(sender, "Use the command '/mk list' to see all racecourse's.");
				return;
			}
			
			RacecourceType type = RacecourceType.Unknown;

			if (args[3].equalsIgnoreCase("lap")) {
				type = RacecourceType.Lap;
			} else if (args[3].equalsIgnoreCase("checkpoint")) {
				type = RacecourceType.CheckPoint;
			}

			if (type == RacecourceType.Unknown) {
				MineKart.output(player, "Unknown racecourse type '" + args[3] + "'. Valid values are 'lap' or 'checkpoint'.");
				return;
			}

			MineKart mineKart = MineKart.getInstance();
			mineKart.createCourse(player, name, type);
			return;
		}

		MineKart.output(player, "Invalid command usage...");
		MineKart.output(player, " - /mk course <coursename> create <type>");
	}
	
	/**
	 * Handle the /mk course <coursename> delete command
	 * 
	 * @param sender The thing that used the command
	 * @param args The command args
	 */
	public static void handleDeleteCommand(CommandSender sender, String[] args) {

		if (!sender.hasPermission("minekart.course.delete")) {
			MineKart.output(sender, "You do not have the required permission 'minekart.course.delete'");
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

			MineKart mineKart = MineKart.getInstance();
			mineKart.deleteCourse(sender, course);
			return;
		}

		MineKart.output(sender, "Invalid command usage...");
		MineKart.output(sender, " - /mk course <coursename> delete");

	}
	
	/**
	 * Handle the /mk course <coursename> <enable|disable> command
	 * 
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
			} else if (args[2].equalsIgnoreCase("disable")) {
				enabled = false;
			} else {
				MineKart.output(sender, "Invalid command usage...");
				MineKart.output(sender, " - /mk course <coursename> <enable|disable>");
				return;
			}

			course.setEnabled(enabled);
			MineKart.output(sender, "The course '" + course.getName() + "' has been " + ChatColor.YELLOW + (enabled ? "Enabled" : "Disabled") + ChatColor.WHITE + ".");
			return;
		}

		MineKart.output(sender, "Invalid command usage...");
		MineKart.output(sender, " - /mk course <coursename> <enable|disable>");

	}

	/**
	 * Handle the /mk course <coursename> [command] command
	 * 
	 * @param sender The thing that used the command
	 * @param args The command args
	 */
	public static void handleCourseCommand(CommandSender sender, String[] args) {
		
		if (args.length <= 2) {
			MineKart.output(sender, "Invalid command usage...");
			MineKart.output(sender, " - /mk course <coursename> <command>");
			MineKart.output(sender, "command: create, delete, enable, disable");
			return;
		}
		
		final String command = args[2];
		
		if (command.equalsIgnoreCase("create")) {
			handleCreateCommand(sender, args);
			return;
		}
		
		if (command.equalsIgnoreCase("delete")) {
			handleDeleteCommand(sender, args);
			return;
		}
		
		if (command.equalsIgnoreCase("enable") || command.equalsIgnoreCase("disable")) {
			handleEnableCommand(sender, args);
			return;
		}
		
		MineKart.output(sender, "Invalid command usage...");
		MineKart.output(sender, " - /mk course <coursename> <command>");
		MineKart.output(sender, "command: create, delete, enable, disable");	
	}

}
