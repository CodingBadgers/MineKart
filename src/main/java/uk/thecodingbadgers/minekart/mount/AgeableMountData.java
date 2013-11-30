package uk.thecodingbadgers.minekart.mount;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

public class AgeableMountData extends DefaultMountData {

	private boolean adult;
	
	protected AgeableMountData(EntityType type) {
		super(type);
	}

	@Override
	public void loadData(ConfigurationSection section) {
		adult = section.getBoolean("adult", true);
		super.loadData(section);
	}

	@Override
	public ConfigurationSection getSaveData(ConfigurationSection section) {
		section.set("adult", adult);
		return super.getSaveData(section);
	}

	@Override
	public void applyMountData(Entity entity) {
		super.applyMountData(entity);
		
		if (!(entity instanceof Ageable)) {
			return;
		}

		Ageable ageable = (Ageable) entity;
		
		if (adult) {
			ageable.setAdult();
		} else {
			ageable.setBaby();
		}
		
		ageable.setAgeLock(true);
	}

	public boolean isAdult() {
		return adult;
	}

}
