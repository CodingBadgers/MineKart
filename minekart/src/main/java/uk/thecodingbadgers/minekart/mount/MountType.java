package uk.thecodingbadgers.minekart.mount;

import com.google.common.collect.Maps;
import org.bukkit.entity.EntityType;

import java.util.Map;

/**
 *
 */
public enum MountType {
    // TODO Add all bukkit EntityType constants

    BAT(EntityType.BAT),
    BLAZE(EntityType.BLAZE),
    CAVE_SPIDER(EntityType.CAVE_SPIDER),
    CHICKEN(EntityType.CHICKEN),
    COW(EntityType.COW),
    CREEPER(EntityType.CREEPER),
    ENDER_DRAGON(EntityType.ENDER_DRAGON),
    ENDERMAN(EntityType.ENDERMAN),
    GHAST(EntityType.GHAST),
    GIANT(EntityType.GIANT),
    HORSE(EntityType.HORSE),
    IRON_GOLEM(EntityType.IRON_GOLEM),
    MAGMA_CUBE(EntityType.MAGMA_CUBE),
    MUSHROOM_COW(EntityType.MUSHROOM_COW),
    OCELOT(EntityType.OCELOT),
    PIG(EntityType.PIG),
    PIG_ZOMBIE(EntityType.PIG_ZOMBIE),
    SKELETON(EntityType.SKELETON),
    SHEEP(EntityType.SHEEP),
    SILVERFISH(EntityType.SILVERFISH),
    SLIME(EntityType.SLIME),
    SNOWMAN(EntityType.SNOWMAN),
    SPIDER(EntityType.SPIDER),
    SQUID(EntityType.SQUID),
    VILLAGER(EntityType.VILLAGER),
    WITCH(EntityType.WITCH),
    WITHER(EntityType.WITHER),
    WOLF(EntityType.WOLF),
    ZOMBIE(EntityType.ZOMBIE),

    FOOT(null);

    private static final Map<String, MountType> ID_MAP = Maps.newHashMap();

    static {
        for (MountType value : values()) {
            if (value.hasBukkitMapping() && value.getBukkitMapping().isSpawnable()) {
                ID_MAP.put(value.getBukkitMapping().getName().toLowerCase(), value);
            }
        }
    }

    private final EntityType mapping;

    private MountType(EntityType mapping) {
        this.mapping = mapping;
    }

    public EntityType getBukkitMapping() {
        return this.mapping;
    }

    public boolean hasBukkitMapping() {
        return this.mapping != null;
    }

    public static MountType fromEntityId(String type) {
        return ID_MAP.get(type.toLowerCase());
    }
}
