package uk.thecodingbadgers.minekart.mount;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.Validate;
import org.bukkit.entity.EntityType;

public class MountDataRegistry {

	private static final Class<? extends MountTypeData> DEFAULT_MOUNT_DATA = DefaultMountData.class;
	private Map<EntityType, Class<? extends MountTypeData>> mountTypeData;

	/**
	 * Instantiates a new racecourse registry.
	 */
	public MountDataRegistry() {
		mountTypeData = new HashMap<EntityType, Class<? extends MountTypeData>>();
	}

	/**
	 * Creates a mount data object for a specific entity type, using the
	 * custom defined data class or the default class.
	 * 
	 * @param type the mount's entity type
	 * @return the mount data object, or null if the id is not registered
	 * @throws NullPointerException if the string id is null
	 */
	public MountTypeData getMountData(EntityType type) throws NullPointerException {
		if (type == null) {
			// TODO special case for on foot races
		}
		
		try {
			Class<? extends MountTypeData> clazz = mountTypeData.get(type);

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
	 * @param id the entity type of the mount
	 * @param clazz the class to handle the custom data for this mount type
	 * @throws NullPointerException if either argument is null
	 */
	public void registerCustomMountData(EntityType type, Class<? extends MountTypeData> clazz) throws NullPointerException {		
		Validate.notNull(type, "Mount type cannot be null");
		Validate.notNull(clazz, "Mount data clazz cannot be null");
		
		mountTypeData.put(type, clazz);
	}

}
