package uk.thecodingbadgers.minekart.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import uk.thecodingbadgers.minekart.MineKart;
import uk.thecodingbadgers.minekart.lang.LangUtils;
import uk.thecodingbadgers.minekart.mount.MountType;
import uk.thecodingbadgers.minekart.racecourse.Racecourse;

public class CreateCommand {

	/**
	 * Handle the /mk set[name]|add[name] <course> command
	 * 
	 * @param sender The thing that used the command
	 * @param args The command args
	 */
	public static void handleSetWarpCommand(CommandSender sender, String[] args) {

		if (!(sender instanceof Player)) {
			LangUtils.sendMessage(sender, "command.error.console");
			return;
		}

		final Player player = (Player) sender;

		if (!player.hasPermission("minekart.create.warp")) {
			LangUtils.sendMessage(sender, "command.error.permission", "minekart.create.warp");
			return;
		}

		// Set commands should be used where only 1 can exist
		// Add commands should be used where multiple can exist
		// set[warpname] <coursename> | add[warpname] <coursename>
		if (args.length == 2) {

			final String coursename = args[1].toLowerCase();
			Racecourse course = MineKart.getInstance().getRacecourse(coursename);
			if (course == null) {
				LangUtils.sendMessage(sender, "command.error.notfound");
				LangUtils.sendMessage(sender, "command.error.list");
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

		LangUtils.sendMessage(sender, "command.error.usage");
		LangUtils.sendMessage(sender, "command.add.usage");
		LangUtils.sendMessage(sender, "command.set.usage");
	}
	
	/**
	 * Handle the /mk delete[name] <course> [id] command
	 * 
	 * @param sender The thing that used the command
	 * @param args The command args
	 */
	public static void handleDeleteWarpCommand(CommandSender sender, String[] args) { // FIXME Command isn't referanced anywhere

		if (!(sender instanceof Player))
			return;

		final Player player = (Player) sender;

		if (!player.hasPermission("minekart.delete.warp")) {
			LangUtils.sendMessage(sender, "command.error.permission", "minekart.delete.warp");
			return;
		}
		
		final String coursename = args[1];
		Racecourse course = MineKart.getInstance().getRacecourse(coursename);
		if (course == null) {
			LangUtils.sendMessage(sender, "command.error.notfound", coursename);
			LangUtils.sendMessage(sender, "command.error.list");
			return;
		}
		
		final String warpName = args[0].substring("delete".length());
		final int id = args.length == 3 ? Integer.parseInt(args[2]) : -1;
		if (course.removeWarp(player, warpName, id)) {
			LangUtils.sendMessage(sender, "command.delete.fail", warpName);
			return;
		}

		LangUtils.sendMessage(sender, "command.delete.success", warpName);
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
			LangUtils.sendMessage(sender, "command.error.permission", "minekart.mount");
			return;
		}

		if (args.length == 4) {

			final String coursename = args[2];
			Racecourse course = MineKart.getInstance().getRacecourse(coursename);
			if (course == null) {
				LangUtils.sendMessage(sender, "command.error.notfound", coursename);
				LangUtils.sendMessage(sender, "command.error.list");
				return;
			}

			final String setting = args[1];
			final String value = args[3];

			if (setting.equalsIgnoreCase("type")) {

                MountType mountType = MountType.FOOT;

				if (!value.equalsIgnoreCase("none")) {

					mountType = MountType.fromEntityId(value);
					if (mountType == null) {
						LangUtils.sendMessage(sender, "command.mount.unknown", value);
						return;
					}

					/*if (!mountType.isAlive()) {
						LangUtils.sendMessage(sender, "command.mount.error.living");
						return;
					}*/
				}

				LangUtils.sendMessage(sender, "command.mount.success", course.getName(), value);
				course.setMountType(mountType);
				return;
			} // TODO add speed setting

			return;
		}

		LangUtils.sendMessage(sender, "command.error.usage");
        LangUtils.sendMessage(sender, "command.mount.usage");
        LangUtils.sendMessage(sender, "command.mount.usage.setting");
	}

}
