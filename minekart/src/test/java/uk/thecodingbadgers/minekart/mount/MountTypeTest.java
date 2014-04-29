package uk.thecodingbadgers.minekart.mount;

import org.bukkit.entity.EntityType;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests to make sure the MountType enum, is properly filled with all the correct values needed.
 */
public class MountTypeTest  {

    @Test
    public void testBukkitMapping() {
        for (MountType value : MountType.values()) {
            if (value.hasBukkitMapping()) {
               assertEquals("Bukkit wrapper for " + value.name() + " is incorrect (" + value.getBukkitMapping().name() + ")", EntityType.valueOf(value.name()), value.getBukkitMapping());
            }
        }
    }

    @Test
    public void testNonSpawnable() {
        for (MountType value : MountType.values()) {
            if (value.hasBukkitMapping()) {
                assertTrue("Mount type " + value.name() + " is not alive", value.getBukkitMapping().isAlive());
                assertTrue("Mount type " + value.name() + " is not spawnable", value.getBukkitMapping().isSpawnable());
            }
        }
    }

    @Test
    public void testMountTypes() {
        for (EntityType entity : EntityType.values()) {
            if (entity.isSpawnable() && entity.isAlive()) {
                try {
                    assertNotNull("Mount mapping missing for " + entity.name(), MountType.valueOf(entity.name()));
                } catch (IllegalArgumentException ex) {
                    fail("Mount mapping missing for " + entity.name());
                }
            } else {
                try {
                    MountType.valueOf(entity.name());
                    fail("Mount type for non living entity (" + entity.name() + ")");
                } catch (IllegalArgumentException ex) {
                    return;
                }
            }
        }
    }

    @Test
    public void testMountFromName() {
        for (EntityType entity : EntityType.values()) {
            if (entity.isSpawnable() && entity.isAlive()) {
                try {
                    assertEquals("MountType#fromEntityId fails for " + entity.name(), entity, MountType.fromEntityId(entity.getName()).getBukkitMapping());
                } catch (Exception ex) {
                    ex.printStackTrace();
                    fail("MountType#fromEntityId fails for " + entity.name());
                }
            }
        }
    }
}