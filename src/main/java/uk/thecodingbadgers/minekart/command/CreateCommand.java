package uk.thecodingbadgers.minekart.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import uk.thecodingbadgers.minekart.MineKart;
import uk.thecodingbadgers.minekart.racecourse.RacecourceType;
import uk.thecodingbadgers.minekart.racecourse.Racecourse;

public class CreateCommand {

	/**
	 * Handle the /mk create course <course> command
	 * 
	 * @param sender The thing that used the command
	 * @param args The command args
	 */
	public static void handleCreateCommand(CommandSender sender, String[] args) {

		if (!(sender instanceof Player))
			return;

		final Player player = (Player) sender;

		if (!player.hasPermission("minekart.create.course")) {
			MineKart.output(player, "You do not have the required permission 'minekart.create.course'");
			return;
		}

		// create arena <name> <type>
		if (args.length == 4) {

			if (!args[1].equalsIgnoreCase("course")) {
				MineKart.output(player, "Invalid command usage...");
				MineKart.output(player, " - /mk create course <name> <type>");
				return;
			}

			final String name = args[2];
			RacecourceType type = RacecourceType.Unknown;

			if (args[3].equalsIgnoreCase("lap")) {
				type = RacecourceType.Lap;
			} else if (args[3].equalsIgnoreCase("checkpoint")) {
				type = RacecourceType.CheckPoint;
			}

			if (type == RacecourceType.Unknown) {
				MineKart.output(player, "Unknown racecourse type '" + args[3] + "'");
				return;
			}

			MineKart mineKart = MineKart.getInstance();
			mineKart.createArena(player, name, type);
			return;
		}

		MineKart.output(player, "Invalid command usage...");
		MineKart.output(player, " - /mk create course <name> <type>");
	}

	/**
	 * Handle the /mk set[name]|add[name] <course> command
	 * 
	 * @param sender The thing that used the command
	 * @param args The command args
	 */
	public static void handleSetWarpCommand(CommandSender sender, String[] args) {

		if (!(sender instanceof Player))
			return;

		final Player player = (Player) sender;

		if (!player.hasPermission("minekart.create.warp")) {
			MineKart.output(player, "You do not have the required permission 'minekart.create.warp'");
			return;
		}

		// Set commands should be used where only 1 can exist
		// Add commands should be used where multiple can exist
		// set[warpname] <coursename> | add[warpname] <coursename>
		if (args.length == 2) {

			final String coursename = args[1].toLowerCase();
			Racecourse course = MineKart.getInstance().getRacecourse(coursename);
			if (course == null) {
				MineKart.output(player, "Could not find a racecourse with the name '" + coursename + "'.");
				return;
			}

			final String mode = args[0].toLowerCase().substring(0, 3);
			final String warpname = args[0].toLowerCase().substring(3);

			if (mode.equalsIgnoreCase("set")) {
				course.setWarp(player, warpname);
				return;
			} else if (mode.equalsIgnoreCase("add")) {
				course.addWarp(player, warpname);
				return;
			}

		}

		MineKart.output(player, "Invalid command usage...");
		MineKart.output(player, " - /mk add[warpname] <coursename>");
		MineKart.output(player, " - /mk set[warpname] <coursename>");
	}

	/**
	 * Handle the /mk mount <setting> <value> <course> command
	 * 
	 * @param sender The thing that used the command
	 * @param args The command args
	 */
	@SuppressWarnings("deprecation")
	public static void handleMountCommand(CommandSender sender, String[] args) {

		if (!sender.hasPermission("minekart.mount")) {
			MineKart.output(sender, "You do not have the required permission 'minekart.mount'");
			return;
		}

		if (args.length == 4) {

			final String coursename = args[3];
			Racecourse course = MineKart.getInstance().getRacecourse(coursename);
			if (course == null) {
				MineKart.output(sender, "Could not find a racecourse with the name '" + coursename + "'.");
				MineKart.output(sender, "Use the command '/mk list' to see all racecourse's.");
				return;
			}

			final String setting = args[1];
			final String value = args[2];

			if (setting.equalsIgnoreCase("type")) {

				EntityType mountType = EntityType.fromName(value);
				if (mountType == null) {
					MineKart.output(sender, "Unknown mount type '" + value + "'");
					return;
				}

				if (!mountType.isAlive()) {
					MineKart.output(sender, "You can only set mounts to be living entities.");
					return;
				}

				MineKart.output(sender, "The mount type for '" + coursename + "' has been set to '" + value + "'.");
				course.setMountType(mountType);
				return;
			}

			return;
		}

		MineKart.output(sender, "Invalid command usage...");
		MineKart.output(sender, " - /mk mount <setting> <value> <coursename>");
		MineKart.output(sender, "setting: type, speed");

	}

}
