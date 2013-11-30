package uk.thecodingbadgers.minekart.command;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.plugin.PluginDescriptionFile;

import uk.thecodingbadgers.minekart.MineKart;

public class CommandHandler implements TabExecutor {

	@Override
	public List<String> onTabComplete(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
		return null;
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
		showPluginHelp(sender);
		return true;
	}

	private void showPluginHelp(CommandSender sender) {
		PluginDescriptionFile pluginDescription = MineKart.getInstance().getDescription();

		MineKart.output(sender, "===================");
		MineKart.output(sender, "MineKart by " + pluginDescription.getAuthors());
		MineKart.output(sender, "Version " + pluginDescription.getVersion());
		MineKart.output(sender, "===================");
		MineKart.output(sender, "/mk course");
		MineKart.output(sender, "/mk mount");
		MineKart.output(sender, "/mk join <coursename>");
		MineKart.output(sender, "/mk leave");
		MineKart.output(sender, "/mk list");
	}
}
