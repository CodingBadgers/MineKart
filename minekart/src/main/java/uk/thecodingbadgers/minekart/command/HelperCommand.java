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
	public static void handleListCommand(Lang lang, CommandSender sender, String[] args) {

		if (!sender.hasPermission("minekart.course.list")) {
			LangUtils.sendMessage(sender, lang, "command.error.permission", "minekart.course.list");
			return;
		}

		Map<String, Racecourse> courses = MineKart.getInstance().getAllRacecourses();

		if (courses.isEmpty()) {
			LangUtils.sendMessage(sender, lang, "command.list.nocourses");
		} else {
			LangUtils.sendMessage(sender, lang, "command.list.header");
			for (Racecourse course : courses.values()) {
				LangUtils.sendMessage(sender, lang, "command.list.entry", course.getName());
			}
		}

	}
	
	/**
	 * Handle the /mk reload command
	 * 
	 * @param sender The thing that used the command
	 * @param args The command args
	 */
	public static void handleReloadCommand(Lang lang, CommandSender sender, String[] args) {

		if (!sender.hasPermission("minekart.reload")) {
			LangUtils.sendMessage(sender, lang, "command.error.permission", "minekart.reload");
			return;
		}
		
		MineKart.getInstance().reload();
		LobbySignManager.updateSigns();
		
		LangUtils.sendMessage(sender, lang, "command.reload.success");
	}

	/**
	 * Handle the /mk info <coursename> command
	 * 
	 * @param sender The thing that used the command
	 * @param args The command args
	 */
	public static void handleInfoCommand(Lang lang, CommandSender sender, String[] args) {

		if (!sender.hasPermission("minekart.course.info")) {
			LangUtils.sendMessage(sender, lang, "command.error.permission", "minekart.course.info");
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

			course.outputInformation(sender);
			return;
		}


		LangUtils.sendMessage(sender, lang, "command.error.usage");
		LangUtils.sendMessage(sender, lang, "command.info.usage");
	}

}
