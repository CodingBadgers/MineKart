package uk.thecodingbadgers.minekart.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import uk.thecodingbadgers.minekart.MineKart;
import uk.thecodingbadgers.minekart.jockey.Jockey;
import uk.thecodingbadgers.minekart.race.Race;
import uk.thecodingbadgers.minekart.racecourse.Racecourse;

public class RaceCommand {

	/**
	 * Handle the /mk join <course> command
	 * 
	 * @param sender The thing that used the command
	 * @param args The command args
	 */
	public static void handleJoinCommand(CommandSender sender, String[] args) {

		if (!(sender instanceof Player))
			return;

		Player player = (Player) sender;
		if (!player.hasPermission("minekart.join")) {
			MineKart.output(player, "You do not have the required permission 'minekart.join'");
			return;
		}

		if (args.length == 2) {

			final String coursename = args[1];
			Racecourse course = MineKart.getInstance().getRacecourse(coursename);
			if (course == null) {
				MineKart.output(player, "Could not find a racecourse with the name '" + coursename + "'.");
				MineKart.output(player, "Use the command '/mk list' to see all racecourse's.");
				return;
			}
			
			if (!player.hasPermission("minekart.join." + course.getName().toLowerCase())) {
				MineKart.output(player, "You do not have the required permission 'minekart.join." + course.getName().toLowerCase() + "'");
				return;
			}


			if (MineKart.getInstance().getJockey(player) != null) {
				MineKart.output(player, "You are already in a race, please leave your current race before joining a new one.");
				return;
			}

			Race race = course.getRace();
			race.addJockey(player);
			return;
		}

		MineKart.output(sender, "Invalid command usage...");
		MineKart.output(sender, " - /mk join <coursename>");

	}

	/**
	 * Handle the /mk forcestart <course> command
	 * 
	 * @param sender The thing that used the command
	 * @param args The command args
	 */
	public static void handleForceStartCommand(CommandSender sender, String[] args) {

		if (!sender.hasPermission("minekart.forcestart")) {
			MineKart.output(sender, "You do not have the required permission 'minekart.forcestart'");
			return;
		}

		if (args.length == 2) {

			final String coursename = args[1];
			Racecourse course = MineKart.getInstance().getRacecourse(coursename);
			if (course == null) {
				MineKart.output(sender, "Could not find a racecourse with the name '" + coursename + "'.");
				MineKart.output(sender, "Use the command '/mk list' to see all racecourse's.");
				return;
			}

			course.getRace().teleportToSpawns();
			return;
		}

		MineKart.output(sender, "Invalid command usage...");
		MineKart.output(sender, " - /mk forcestart <coursename>");

	}

	/**
	 * Handle the /mk leave command
	 * 
	 * @param sender The thing that used the command
	 * @param args The command args
	 */
	public static void handleLeaveCommand(CommandSender sender, String[] args) {

		if (!(sender instanceof Player))
			return;

		Player player = (Player) sender;
		if (!player.hasPermission("minekart.join")) {
			MineKart.output(player, "You do not have the required permission 'minekart.join'");
			return;
		}

		Jockey jockey = MineKart.getInstance().getJockey(player);
		if (jockey == null) {
			MineKart.output(player, "You are not in a race. To join a race use '/mk join <coursename>'.");
			return;
		}

		jockey.getRace().removeJockey(jockey);
		MineKart.output(player, "You have left the race.");
	}

}
