package uk.thecodingbadgers.minekart.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import uk.thecodingbadgers.minekart.MineKart;
import uk.thecodingbadgers.minekart.lang.Lang;
import uk.thecodingbadgers.minekart.lang.LangUtils;
import uk.thecodingbadgers.minekart.racecourse.Racecourse;

public class CourseCommand {

	/**
	 * Handle the /mk course <coursename> [command] command
	 * 
	 * @param sender The thing that used the command
	 * @param args The command args
	 */
	public static void handleCourseCommand(Lang lang, CommandSender sender, String[] args) {

		if (args.length <= 2) {
			LangUtils.sendMessage(sender, lang, "command.error.usage");
			LangUtils.sendMessage(sender, lang, "command.course.usage");
			LangUtils.sendMessage(sender, lang, "command.course.usage.commands");
			return;
		}

		final String command = args[2];

		if (command.equalsIgnoreCase("create")) {
			handleCreateCommand(sender, lang, args);
			return;
		}

		if (command.equalsIgnoreCase("delete")) {
			handleDeleteCommand(sender, lang, args);
			return;
		}

		if (command.equalsIgnoreCase("enable") || command.equalsIgnoreCase("disable")) {
			handleEnableCommand(sender, lang, args);
			return;
		}

		if (command.startsWith("show")) {
			handleShowCommand(sender, lang, args);
			return;
		}

		LangUtils.sendMessage(sender, lang, "command.error.usage");
		LangUtils.sendMessage(sender, lang, "command.course.usage");
		LangUtils.sendMessage(sender, lang, "command.course.usage.commands");
	}
	
	/**
	 * Handle the /mk course <coursename> create <type> command
	 * 
	 * @param sender The thing that used the command
	 * @param args The command args
	 */
	private static void handleCreateCommand(CommandSender sender, Lang lang, String[] args) {

		if (!(sender instanceof Player)) {
			LangUtils.sendMessage(sender, lang, "command.error.console");
			return;
		}

		final Player player = (Player) sender;

		if (!player.hasPermission("minekart.course.create")) {
			LangUtils.sendMessage(sender, lang, "command.error.permission", "minekart.course.create");
			return;
		}

		// course <coursename> create <type>
		if (args.length == 4) {

			final String name = args[1];
			Racecourse course = MineKart.getInstance().getRacecourse(name);
			if (course != null) {
				LangUtils.sendMessage(sender, lang, "command.create.error.exists", name);
				LangUtils.sendMessage(sender, lang, "command.error.list");
				return;
			}

			MineKart mineKart = MineKart.getInstance();
			mineKart.createCourse(player, name, args[3]);
			return;
		}

		LangUtils.sendMessage(sender, lang, "command.error.usage");
		LangUtils.sendMessage(sender, lang, "command.create.usage");
	}

	/**
	 * Handle the /mk course <coursename> delete command
	 * 
	 * @param sender The thing that used the command
	 * @param args The command args
	 */
	private static void handleDeleteCommand(CommandSender sender, Lang lang, String[] args) {

		if (!sender.hasPermission("minekart.course.delete")) {
			LangUtils.sendMessage(sender, lang, "command.error.permission", "minekart.course.delete");
			return;
		}

		if (args.length == 3) {

			final String coursename = args[1];
			Racecourse course = MineKart.getInstance().getRacecourse(coursename);
			if (course == null) {
				LangUtils.sendMessage(sender, lang, "command.error.notfound", coursename);
				LangUtils.sendMessage(sender, lang, "command.error.list");
				return;
			}

			MineKart mineKart = MineKart.getInstance();
			mineKart.deleteCourse(sender, course);
			return;
		}

		LangUtils.sendMessage(sender, lang, "command.error.usage");
		LangUtils.sendMessage(sender, lang, "command.delete.usage");

	}

	/**
	 * Handle the /mk course <coursename> <enable|disable> command
	 * 
	 * @param sender The thing that used the command
	 * @param args The command args
	 */
	private static void handleEnableCommand(CommandSender sender, Lang lang, String[] args) {

		if (!sender.hasPermission("minekart.course.enable")) {
			LangUtils.sendMessage(sender, lang, "command.error.permission", "minekart.course.enable");
			return;
		}

		if (args.length == 3) {

			final String coursename = args[1];
			Racecourse course = MineKart.getInstance().getRacecourse(coursename);
			if (course == null) {
				LangUtils.sendMessage(sender, lang, "command.error.notfound", coursename);
				LangUtils.sendMessage(sender, lang, "command.error.list");
				return;
			}

			boolean enabled;
			if (args[2].equalsIgnoreCase("enable")) {
				enabled = true;
			} else if (args[2].equalsIgnoreCase("disable")) {
				enabled = false;
			} else {
				LangUtils.sendMessage(sender, lang, "command.error.usage");
				LangUtils.sendMessage(sender, lang, "command.state.usage");
				return;
			}

			course.setEnabled(enabled);
			LangUtils.sendMessage(sender, lang, "command.state.success", course.getName(), enabled ? lang.getTranslation("command.state.enabled") : lang.getTranslation("command.state.disabled"));
			return;
		}

		LangUtils.sendMessage(sender, lang, "command.error.usage");
		LangUtils.sendMessage(sender, lang, "command.state.usage");

	}

	/**
	 * Handle the /mk course <coursename> show[warp type] command
	 * 
	 * @param sender
	 * @param args
	 */
	private static void handleShowCommand(CommandSender sender, Lang lang, String[] args) {

		if (args.length == 3) {

			String warptype = args[2].substring("show".length());

			if (!sender.hasPermission("minekart.course.show." + warptype)) {
				LangUtils.sendMessage(sender, lang, "command.error.permission", "minekart.course.show." + warptype);
				return;
			}

			if (!(sender instanceof Player)) {
				LangUtils.sendMessage(sender, lang, "command.error.console");
				return;
			}

			Player player = (Player) sender;

			final String coursename = args[1];
			Racecourse course = MineKart.getInstance().getRacecourse(coursename);
			if (course == null) {
				LangUtils.sendMessage(sender, lang, "command.error.notfound", coursename);
				LangUtils.sendMessage(sender, lang, "command.error.list");
				return;
			}

			if (course.showWarps(player, warptype)) {
				LangUtils.sendMessage(sender, lang, "command.show.success", warptype);
			} else {
				LangUtils.sendMessage(sender, lang, "command.show.unknown", warptype);
			}
			return;
		}

		LangUtils.sendMessage(sender, lang, "command.error.usage");
		LangUtils.sendMessage(sender, lang, "command.show.usage");
	}


}
