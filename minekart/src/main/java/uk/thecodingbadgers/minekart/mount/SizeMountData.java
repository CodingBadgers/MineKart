package uk.thecodingbadgers.minekart.mount;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Slime;

public class SizeMountData extends DefaultMountData {

	private int size;

	protected SizeMountData(EntityType type) {
		super(type);
	}

	@Override
	public void loadData(ConfigurationSection section) {
		super.loadData(section);
		this.size = section.getInt("size", 1);
	}

	@Override
	public ConfigurationSection getSaveData(ConfigurationSection section) {
		section.set("size", this.size);
		return super.getSaveData(section);
	}

	@Override
	public void applyMountData(Entity npc) {
		super.applyMountData(npc);

		if (!(npc instanceof Slime)) {
			return;
		}

		Slime slime = (Slime) npc;
		
		slime.setSize(size);
	}

	public int getSize() {
		return size;
	}

}
