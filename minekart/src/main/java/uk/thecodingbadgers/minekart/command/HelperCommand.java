package uk.thecodingbadgers.minekart.command;

import java.util.Map;

import org.bukkit.command.CommandSender;

import uk.thecodingbadgers.minekart.MineKart;
import uk.thecodingbadgers.minekart.lang.Lang;
import uk.thecodingbadgers.minekart.lang.LangUtils;
import uk.thecodingbadgers.minekart.lobby.LobbySignManager;
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
			LangUtils.sendMessage(sender, "command.error.permission", "minekart.course.list");
			return;
		}

		Map<String, Racecourse> courses = MineKart.getInstance().getAllRacecourses();

		if (courses.isEmpty()) {
			LangUtils.sendMessage(sender, "command.list.nocourses");
		} else {
			LangUtils.sendMessage(sender, "command.list.header");
			for (Racecourse course : courses.values()) {
				LangUtils.sendMessage(sender, "command.list.entry", course.getName());
			}
		}

	}
	
	/**
	 * Handle the /mk reload command
	 * 
	 * @param sender The thing that used the command
	 * @param args The command args
	 */
	public static void handleReloadCommand(CommandSender sender, String[] args) {

		if (!sender.hasPermission("minekart.reload")) {
			LangUtils.sendMessage(sender, "command.error.permission", "minekart.reload");
			return;
		}
		
		MineKart.getInstance().reload();
		LobbySignManager.updateSigns();
		
		LangUtils.sendMessage(sender, "command.reload.success");
	}

	/**
	 * Handle the /mk info <coursename> command
	 * 
	 * @param sender The thing that used the command
	 * @param args The command args
	 */
	public static void handleInfoCommand(CommandSender sender, String[] args) {

		if (!sender.hasPermission("minekart.course.info")) {
			LangUtils.sendMessage(sender, "command.error.permission", "minekart.course.info");
			return;
		}

		if (args.length == 2) {
			final String coursename = args[1];
			Racecourse course = MineKart.getInstance().getRacecourse(coursename);
			if (course == null) {
				LangUtils.sendMessage(sender, "command.error.notfound", coursename);
				LangUtils.sendMessage(sender, "command.error.list");
				return;
			}

			course.outputInformation(sender);
			return;
		}


		LangUtils.sendMessage(sender, "command.error.usage");
		LangUtils.sendMessage(sender, "command.info.usage");
	}

}
