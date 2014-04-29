package uk.thecodingbadgers.minekart.mount;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

/**
 * Created by James on 28/04/2014.
 */
public class FootMountData extends MountTypeData {
    protected FootMountData(EntityType type) {
        super(type);
    }

    @Override
    public void applyMountData(Entity npc) {

    }

    @Override
    public void loadData(ConfigurationSection section) {

    }

    @Override
    public ConfigurationSection getSaveData(ConfigurationSection section) {
        return null;
    }
}
