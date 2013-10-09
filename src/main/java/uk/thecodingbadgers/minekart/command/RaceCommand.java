package uk.thecodingbadgers.minekart.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import uk.thecodingbadgers.minekart.MineKart;
import uk.thecodingbadgers.minekart.race.Race;
import uk.thecodingbadgers.minekart.racecourse.Racecourse;

public class RaceCommand {

	/**
	 * Handle the /mk join <course> command
	 * @param sender The thing that used the command
	 * @param args The command args
	 */
	public static void handleJoinCommand(CommandSender sender, String[] args) {
		
		if (!(sender instanceof Player))
			return;
		
		Player player = (Player)sender;
		if (!player.hasPermission("minekart.join")) {
			MineKart.output(player, "You do not have the required permission 'minekart.join'");
			return;
		}
		
		if (args.length == 2) {
			
			final String coursename = args[1];
			Racecourse course = MineKart.getInstance().getRacecourse(coursename);
			if (course == null) {
				MineKart.output(player, "Could not find a racecourse with the name '" + coursename + "'.");
				MineKart.output(player, "Use the command '/mk list' to see all racecourse's.");
				return;
			}
			
			Race race = course.getRace();
			race.addJockey(player);
			return;
		}
		
		MineKart.output(sender, "Invalid command usage...");
		MineKart.output(sender, " - /mk join <coursename>");	
		
	}

}
