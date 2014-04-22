package uk.thecodingbadgers.minekart.powerup.damageeffect;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.citizensnpcs.api.npc.NPC;
import uk.thecodingbadgers.minekart.MineKart;
import uk.thecodingbadgers.minekart.jockey.Jockey;
import uk.thecodingbadgers.minekart.jockey.Mount;
import uk.thecodingbadgers.minekart.util.FireworkFactory;
import uk.thecodingbadgers.minekart.version.NmsHandler;

public class DamageEffectFreeze extends DamageEffect {

	@Override
	public void use(Jockey jockey) {
		
		final Player player = jockey.getPlayer();
		final NPC mount = jockey.getMount();
		
		if (mount != null) {
			Mount trait = mount.getTrait(NmsHandler.getNmsHandler().getMountClass());
			trait.setEnabled(false); 
		}
		else {
			player.setWalkSpeed(0.0f);
		}
		
		final ItemStack oldHelmet = player.getInventory().getHelmet();
		player.getInventory().setHelmet(new ItemStack(Material.ICE));
		MineKart.output(player, ChatColor.RED + "You have been frozen!");
		FireworkFactory.SpawnFireworkExplosion(player.getEyeLocation(), FireworkEffect.Type.BALL, Color.BLUE);
		
		final int freezeLength = 1;
		Bukkit.getScheduler().scheduleSyncDelayedTask(MineKart.getInstance(), new Runnable() {

			@Override
			public void run() {
				if (mount != null) {
					Mount trait = mount.getTrait(NmsHandler.getNmsHandler().getMountClass());
					trait.setEnabled(true); 
				}
				else {
					player.setWalkSpeed(0.0f);
				}
				
				player.getInventory().setHelmet(oldHelmet);
			}
			
		}, freezeLength * 20L);
		
	}

}
