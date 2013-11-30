package uk.thecodingbadgers.minekart.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import uk.thecodingbadgers.minekart.MineKart;
import uk.thecodingbadgers.minekart.racecourse.Racecourse;

public class CreateCommand {

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
	 * Handle the /mk delete[name] <course> [id] command
	 * 
	 * @param sender The thing that used the command
	 * @param args The command args
	 */
	public static void handleDeleteWarpCommand(CommandSender sender, String[] args) {

		if (!(sender instanceof Player))
			return;

		final Player player = (Player) sender;

		if (!player.hasPermission("minekart.delete.warp")) {
			MineKart.output(player, "You do not have the required permission 'minekart.delete.warp'");
			return;
		}
		
		final String coursename = args[1];
		Racecourse course = MineKart.getInstance().getRacecourse(coursename);
		if (course == null) {
			MineKart.output(sender, "Could not find a racecourse with the name '" + coursename + "'.");
			MineKart.output(sender, "Use the command '/mk list' to see all racecourse's.");
			return;
		}
		
		final String warpName = args[0].substring("delete".length());
		final int id = args.length == 3 ? Integer.parseInt(args[2]) : -1;
		if (course.removeWarp(player, warpName, id)) {
			MineKart.output(player, "Warp removed.");
			return;
		}
		
		MineKart.output(player, "Failed to remove warp.");		
	}

	/**
	 * Handle the /mk mount <setting> <course> <value> command
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

			final String coursename = args[2];
			Racecourse course = MineKart.getInstance().getRacecourse(coursename);
			if (course == null) {
				MineKart.output(sender, "Could not find a racecourse with the name '" + coursename + "'.");
				MineKart.output(sender, "Use the command '/mk list' to see all racecourse's.");
				return;
			}

			final String setting = args[1];
			final String value = args[3];

			if (setting.equalsIgnoreCase("type")) {

				EntityType mountType = EntityType.UNKNOWN;

				if (!value.equalsIgnoreCase("none")) {

					mountType = EntityType.fromName(value);
					if (mountType == null) {
						MineKart.output(sender, "Unknown mount type '" + value + "'");
						return;
					}

					if (!mountType.isAlive()) {
						MineKart.output(sender, "You can only set mounts to be living entities.");
						return;
					}
				}

				MineKart.output(sender, "The mount type for '" + course.getName() + "' has been set to '" + value + "'.");
				course.setMountType(mountType);
				return;
			}

			return;
		}

		MineKart.output(sender, "Invalid command usage...");
		MineKart.output(sender, " - /mk mount <setting> <coursename> <value>");
		MineKart.output(sender, "setting: type, speed");

	}

}
