package uk.thecodingbadgers.minekart.powerup.damageeffect;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.citizensnpcs.api.npc.NPC;
import uk.thecodingbadgers.minekart.MineKart;
import uk.thecodingbadgers.minekart.jockey.ControllableMount;
import uk.thecodingbadgers.minekart.jockey.Jockey;

public class DamageEffectFreeze extends DamageEffect {

	@Override
	public void use(Jockey jockey) {
		
		final Player player = jockey.getPlayer();
		final NPC mount = jockey.getMount();
		
		if (mount != null) {
			ControllableMount trait = mount.getTrait(ControllableMount.class);
			trait.setEnabled(false); 
		}
		else {
			player.setWalkSpeed(0.0f);
		}
		
		final ItemStack oldHelmet = player.getInventory().getHelmet();
		player.getInventory().setHelmet(new ItemStack(Material.ICE));
		MineKart.output(player, "You have been frozen!");
		
		final int freezeLength = 1;
		Bukkit.getScheduler().scheduleSyncDelayedTask(MineKart.getInstance(), new Runnable() {

			@Override
			public void run() {
				if (mount != null) {
					ControllableMount trait = mount.getTrait(ControllableMount.class);
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
