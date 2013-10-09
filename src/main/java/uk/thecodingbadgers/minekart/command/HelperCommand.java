package uk.thecodingbadgers.minekart.command;

import java.util.Map;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import uk.thecodingbadgers.minekart.MineKart;
import uk.thecodingbadgers.minekart.racecourse.Racecourse;

public class HelperCommand {

	public static void handleListCommand(CommandSender sender, String[] args) {
		
		if (!(sender instanceof Player))
			return;
		
		final Player player = (Player)sender;
		
		if (!player.hasPermission("minekart.course.list")) {
			MineKart.output(player, "You do not have the required permission 'minekart.course.list'");
			return;
		}
		
		Map<String, Racecourse> courses = MineKart.getInstance().getAllRacecourses();
		
		if (courses.isEmpty()) {
			MineKart.output(player, "No racecourses exists.");
		}
		else {
			MineKart.output(player, "The following racecourses exists...");			
			for (Racecourse course : courses.values()) {
				MineKart.output(player, " - "  + course.getName());
			}
		}
		
	}

}
