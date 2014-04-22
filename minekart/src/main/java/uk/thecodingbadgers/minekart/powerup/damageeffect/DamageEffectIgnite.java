package uk.thecodingbadgers.minekart.powerup.damageeffect;

import uk.thecodingbadgers.minekart.jockey.Jockey;

public class DamageEffectIgnite extends DamageEffect {

	@Override
	public void use(Jockey jockey) {
		
		final int fireLength = 6;
		jockey.getPlayer().setFireTicks(fireLength * 20);
		
	}

}
