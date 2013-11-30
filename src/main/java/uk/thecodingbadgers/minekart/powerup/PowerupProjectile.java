package uk.thecodingbadgers.minekart.powerup;

import java.io.File;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import uk.thecodingbadgers.minekart.MineKart;
import uk.thecodingbadgers.minekart.jockey.Jockey;

public class PowerupProjectile extends Powerup {

	/** Type of projectile to launch **/
	private EntityType type;
	
	/** The speed of the projectile **/
	private double speed;
	
	/** The effect to be applied on contact with another player **/
	private DamageEffect damageEffect;
	
	/** The number of hearts damage to do on contact with another player **/
	private double damage;

	/**
	 * Class constructor
	 */
	public PowerupProjectile() {
		this.useMode = PowerupUseMode.Projectile;
	}

	/**
	 * Copy constructor
	 * 
	 * @param powerup Powerup to copy from
	 */
	public PowerupProjectile(PowerupProjectile powerup) {
		super(powerup);
		this.speed = powerup.speed;
		this.type = powerup.type;
		this.damage = powerup.damage;
		this.damageEffect = powerup.damageEffect;
	}

	/**
	 * Load the powerup
	 * 
	 * @param configfile The file containing the powerup data
	 */
	public void load(File configfile) {
		super.load(configfile);

		FileConfiguration file = YamlConfiguration.loadConfiguration(configfile);

		this.speed = file.getDouble("powerup.projectile.speed");
		this.type = EntityType.valueOf(file.getString("powerup.projectile.type"));
		this.damage = file.getDouble("powerup.projectile.damage", -1);
		
		try {
			this.damageEffect = DamageEffect.valueOf(file.getString("powerup.projectile.damageeffect", "None"));
		} catch(Exception ex) {
			this.damageEffect = DamageEffect.None;
		} finally {
			if (this.damageEffect == null) {
				this.damageEffect = DamageEffect.None;
			}
		}
	}

	/**
	 * Called when the powerup is used
	 * 
	 * @param jockey The player who used it
	 */
	@Override
	public void onUse(Jockey jockey) {

		this.amount--;

		final Player player = jockey.getPlayer();
		final World world = player.getWorld();
		final Location location = player.getEyeLocation();		
		
		Projectile projectile = (Projectile)world.spawnEntity(location, type);
		projectile.setVelocity(player.getLocation().getDirection().multiply(this.speed));
		projectile.setShooter(player);
		
		projectile.setMetadata("powerup", new FixedMetadataValue(MineKart.getInstance(), this));
		
	}
	
	/**
	 * Called when the powerup does damage to another entity
	 * @param entityAttackEvent The entity damage by entity event 
	 */
	@Override
	public void onDamageEntity(EntityDamageByEntityEvent entityAttackEvent) {
	
		LivingEntity entity = (LivingEntity)entityAttackEvent.getEntity();
		
		if (this.damage >= 0) {
			// We store damage as heart damage. So multiply it by 2.
			entity.setHealth(entity.getHealth() - (this.damage * 2.0));
			entityAttackEvent.setDamage(0.0);
		}
		
		if (this.damageEffect != DamageEffect.None) {
						
			switch (this.damageEffect)
			{
			case Inferno:
				{
					final int fireLength = 6;
					entity.setFireTicks(fireLength * 20);
				}
				break;
			case None:
				break;
			case Poison:
				{
					PotionEffect poisonPotion = new PotionEffect(PotionEffectType.POISON, 3 * 20, 1);
					entity.addPotionEffect(poisonPotion);
				}
				break;
			default:
				break;			
			}
		}
		
	}

}
