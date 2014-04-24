package uk.thecodingbadgers.minekart.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import uk.thecodingbadgers.minekart.MineKart;
import uk.thecodingbadgers.minekart.jockey.Jockey;
import uk.thecodingbadgers.minekart.lang.Lang;
import uk.thecodingbadgers.minekart.lang.LangUtils;
import uk.thecodingbadgers.minekart.race.Race;
import uk.thecodingbadgers.minekart.racecourse.Racecourse;

public class RaceCommand {

	/**
	 * Handle the /mk join <course> command
	 * 
	 * @param sender The thing that used the command
	 * @param args The command args
	 */
	public static void handleJoinCommand(Lang lang, CommandSender sender, String[] args) {

		if (!(sender instanceof Player))
			return;

		Player player = (Player) sender;
		if (!player.hasPermission("minekart.join")) {
			LangUtils.sendMessage(sender, lang, "command.error.permission", "minekart.join");
			return;
		}

		if (args.length == 2) {

			final String coursename = args[1];
			Racecourse course = MineKart.getInstance().getRacecourse(coursename);
			if (course == null) {
				LangUtils.sendMessage(sender, lang, "command.error.notfound", coursename);
				LangUtils.sendMessage(sender, lang, "command.error.list");
				return;
			}
			
			if (!player.hasPermission("minekart.join." + course.getName().toLowerCase())) {
				LangUtils.sendMessage(sender, lang, "command.error.permission", "minekart.join" + course.getName().toLowerCase());
				return;
			}


			if (MineKart.getInstance().getJockey(player) != null) {
				LangUtils.sendMessage(sender, lang, "command.join.inrace");
				return;
			}

			Race race = course.getRace();
			race.addJockey(player);
			return;
		}

		LangUtils.sendMessage(sender, lang, "command.error.usage");
		LangUtils.sendMessage(sender, lang, "command.join.usage");

	}

	/**
	 * Handle the /mk forcestart <course> command
	 * 
	 * @param sender The thing that used the command
	 * @param args The command args
	 */
	public static void handleForceStartCommand(Lang lang, CommandSender sender, String[] args) {

		if (!sender.hasPermission("minekart.forcestart")) {
			LangUtils.sendMessage(sender, lang, "command.error.permission", "minekart.forcestart");
			return;
		}

		if (args.length == 2) {

			final String coursename = args[1];
			Racecourse course = MineKart.getInstance().getRacecourse(coursename);
			if (course == null) {
				LangUtils.sendMessage(sender, lang, "command.error.notfound", coursename);
				LangUtils.sendMessage(sender, lang, "command.error.list");
				return;
			}

			course.getRace().teleportToSpawns();
			return;
		}

		LangUtils.sendMessage(sender, lang, "command.error.usage");
		LangUtils.sendMessage(sender, lang, "command.start.usage");

	}

	/**
	 * Handle the /mk leave command
	 * 
	 * @param sender The thing that used the command
	 * @param args The command args
	 */
	public static void handleLeaveCommand(Lang lang, CommandSender sender, String[] args) {

		if (!(sender instanceof Player))
			return;

		Player player = (Player) sender;
		if (!player.hasPermission("minekart.join")) {
			LangUtils.sendMessage(sender, lang, "command.error.permission", "minekart.join");
			return;
		}

		Jockey jockey = MineKart.getInstance().getJockey(player);
		if (jockey == null) {
			LangUtils.sendMessage(sender, lang, "command.leave.norace");
			return;
		}

		jockey.getRace().removeJockey(jockey);
		LangUtils.sendMessage(sender, lang, "command.leave.sucess");
	}

}
