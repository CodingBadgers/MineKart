package uk.thecodingbadgers.minekart.command;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.util.ChatPaginator;

import com.google.common.base.Splitter;

import uk.thecodingbadgers.minekart.MineKart;
import uk.thecodingbadgers.minekart.lang.Lang;
import uk.thecodingbadgers.minekart.lang.LangUtils;

public class CommandHandler implements TabExecutor {

	private static final char NEW_LINE = '\n';
	private static String seperator;

    public static final int SEPARATOR_WIDTH = 10;

    static {
		StringBuilder separator = new StringBuilder();
		for (int i = 0; i < SEPARATOR_WIDTH; i++) {
			separator.append(LangUtils.getLang().getTranslation("command.help.header.separator"));
		}
		CommandHandler.seperator = separator.toString();
	}
	
	@Override
	public List<String> onTabComplete(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
		return null; // TODO tab completion
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		if (!command.getName().equalsIgnoreCase("minekart")) {
			return false;
		}

		// If no arguments are passed show the info and help
		if (args.length == 0) {
			showPluginHelp(sender);
			return true;
		}

		final String controlArgument = args[0].toLowerCase();

		// if the control argument is create, let the create command handler take care of it
		if (controlArgument.startsWith("course")) {
			CourseCommand.handleCourseCommand(sender, args);
			return true;
		}
		
		// if the control argument is create, let the create command handler take care of it
		if (controlArgument.startsWith("times")) {
			StatsCommand.handleTimesCommand(sender, args);
			return true;
		}

		// if the control argument is set or add, let the setwarp command handler take care of it
		if (controlArgument.startsWith("set") || controlArgument.startsWith("add")) {
			CreateCommand.handleSetWarpCommand(sender, args);
			return true;
		}

		// if the control argument is mount, let the setwarp command handler take care of it
		if (controlArgument.startsWith("mount")) {
			CreateCommand.handleMountCommand(sender, args);
			return true;
		}

		// if the control argument is list, let the helper command handler take care of it
		if (controlArgument.startsWith("list")) {
			HelperCommand.handleListCommand(sender, args);
			return true;
		}

		// if the control argument is info, let the helper command handler take care of it
		if (controlArgument.startsWith("info")) {
			HelperCommand.handleInfoCommand(sender, args);
			return true;
		}
		
		// if the control argument is info, let the helper command handler take care of it
		if (controlArgument.startsWith("reload")) {
			HelperCommand.handleReloadCommand(sender, args);
			return true;
		}

		// if the control argument is join, let the race command handler take care of it
		if (controlArgument.startsWith("join") || controlArgument.startsWith("j")) {
			RaceCommand.handleJoinCommand(sender, args);
			return true;
		}

		// if the control argument is leave, let the race command handler take care of it
		if (controlArgument.startsWith("leave") || controlArgument.startsWith("l")) {
			RaceCommand.handleLeaveCommand(sender, args);
			return true;
		}

		// if the control argument is forcestart, let the race command handler take care of it
		if (controlArgument.startsWith("forcestart") || controlArgument.startsWith("fs")) {
			RaceCommand.handleForceStartCommand(sender, args);
			return true;
		}

		// Unknown command
		return true;
	}

	private void showPluginHelp(CommandSender sender) { // TODO better help generation
        Lang lang = LangUtils.getLang(sender);
		PluginDescriptionFile pluginDescription = MineKart.getInstance().getDescription();

		StringBuilder message = new StringBuilder();
		
		message.append(seperator).append(NEW_LINE);
		message.append(lang.getTranslation("command.help.header.authors", pluginDescription.getName(), pluginDescription.getAuthors())).append(NEW_LINE);
		message.append(lang.getTranslation("command.help.header.version", pluginDescription.getVersion())).append(NEW_LINE);
		message.append(seperator).append(NEW_LINE);
		
		message.append(lang.getTranslation("command.help.course")).append(NEW_LINE);
		message.append(lang.getTranslation("command.help.mount")).append(NEW_LINE);
		message.append(lang.getTranslation("command.help.join")).append(NEW_LINE);
		message.append(lang.getTranslation("command.help.leave")).append(NEW_LINE);
		message.append(lang.getTranslation("command.help.list")).append(NEW_LINE);
		
		for (String line : Splitter.on(NEW_LINE).split(message.toString())) {
			sender.sendMessage(LangUtils.formatMessage(lang, line));
		}
	}
}
