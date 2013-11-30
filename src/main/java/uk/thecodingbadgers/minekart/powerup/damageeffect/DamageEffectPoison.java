package uk.thecodingbadgers.minekart.powerup.damageeffect;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import uk.thecodingbadgers.minekart.jockey.Jockey;

public class DamageEffectPoison extends DamageEffect {

	@Override
	public void use(Jockey jockey) {
		
		final int length = 3;
		final int multiplier = 1;
		
		PotionEffect poisonPotion = new PotionEffect(PotionEffectType.POISON, length * 20, multiplier);
		jockey.getPlayer().addPotionEffect(poisonPotion);
		
	}

}
