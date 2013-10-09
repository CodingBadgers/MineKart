package uk.thecodingbadgers.minekart.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import uk.thecodingbadgers.minekart.MineKart;
import uk.thecodingbadgers.minekart.racecourse.RacecourceType;

public class CreateCommand {
	
	public static void handleCommand(CommandSender sender, String[] args) {
		
		if (!(sender instanceof Player))
			return;
		
		final Player player = (Player)sender;
		
		if (!player.hasPermission("minekart.create")) {
			MineKart.output(player, "You do not have the required permission 'minekart.create'");
			return;
		}

		// mk create arena <name> <type>
		if (args.length == 5) {
			
			if (!args[1].equalsIgnoreCase("arena")) {
				MineKart.output(player, "Invalid command usage...");
				MineKart.output(player, " - /mk create arena <name> <type>");
				return;
			}
			
			final String name = args[2];
			final RacecourceType type = RacecourceType.valueOf(args[3]);
			
			if (type == null) {
				MineKart.output(player, "Unknown racecourse type '" + args[3] + "'");
				return;
			}
			
			MineKart mineKart = MineKart.getInstance();		
			mineKart.createArena(player, name, type);	
			return;
		}
		
		MineKart.output(player, "Invalid command usage...");
		MineKart.output(player, " - /mk create arena <name> <type>");
	}

}
