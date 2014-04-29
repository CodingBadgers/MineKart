package uk.thecodingbadgers.minekart.mount;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.Maps;
import org.apache.commons.lang.Validate;
import org.bukkit.entity.EntityType;

public class MountDataRegistry {

	private static final Class<? extends MountTypeData> DEFAULT_MOUNT_DATA = DefaultMountData.class;
	private Map<MountType, Class<? extends MountTypeData>> mountTypeData;
    private Class<? extends MountTypeData> footTypeData;

	/**
	 * Instantiates a new racecourse registry.
	 */
	public MountDataRegistry() {
		mountTypeData = Maps.newHashMap();
	}

	/**
	 * Creates a mount data object for a specific entity type, using the
	 * custom defined data class or the default class.
	 * 
	 * @param type the mount type
	 * @return the mount data object, or null if the id is not registered
	 * @throws NullPointerException if the string id is null
	 */
	public MountTypeData getMountData(MountType type) throws NullPointerException {
		try {
			Class<? extends MountTypeData> clazz = this.mountTypeData.get(type);

            if (clazz == null) {
				clazz = DEFAULT_MOUNT_DATA;
			}

			Constructor<? extends MountTypeData> ctor = clazz.getDeclaredConstructor(EntityType.class);

			if (ctor == null) {
				return null;
			}

			ctor.setAccessible(true);
			return ctor.newInstance(type);
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Register a new mount type and data class.
	 * 
	 * @param type the mount type of the mount
	 * @param clazz the class to handle the custom data for this mount type
	 * @throws NullPointerException if either argument is null
	 */
	public void registerCustomMountData(MountType type, Class<? extends MountTypeData> clazz) throws NullPointerException {
        Validate.notNull(type, "Mount type cannot be null");
		Validate.notNull(clazz, "Mount data clazz cannot be null");

        mountTypeData.put(type, clazz);
	}

}
