package uk.thecodingbadgers.minekart.mount;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

public abstract class MountTypeData {

	protected final EntityType type;

	protected MountTypeData(EntityType type) {
		this.type = type;
	}

	public final EntityType getEntityType() {
		return this.type;
	}

	public abstract void applyMountData(Entity npc);

	public abstract void loadData(ConfigurationSection section);

	public abstract ConfigurationSection getSaveData(ConfigurationSection section);

}
