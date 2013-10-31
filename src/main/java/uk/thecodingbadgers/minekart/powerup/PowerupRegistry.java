package uk.thecodingbadgers.minekart.powerup;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

/**
 * The powerup registry, handles registration and handling of powerup types
 */
public class PowerupRegistry {

	private Map<String, Class<? extends Powerup>> powerupTypes;

	/**
	 * Instantiates a new powerup registry.
	 */
	public PowerupRegistry() {
		powerupTypes = new HashMap<String, Class<? extends Powerup>>();
	}

	/**
	 * Gets the a powerup type by its string id.
	 * 
	 * @param poweruptype the string id
	 * @return the powerup type instance
	 */
	public Powerup getPowerupType(String poweruptype) {
		try {
			Class<? extends Powerup> clazz = powerupTypes.get(poweruptype);

			if (clazz == null) {
				return null;
			}

			return clazz.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Clone a powerup.
	 * 
	 * @param <T> the generic type of the powerup
	 * @param powerup the powerup to clone
	 * @return the cloned powerup
	 */
	@SuppressWarnings("unchecked")
	public <T extends Powerup> T clonePowerup(T powerup) {
		try {
			Constructor<? extends T> ctor = (Constructor<? extends T>) powerup.getClass().getConstructor(powerup.getClass());
			return ctor.newInstance(powerup);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Register a new custom powerup type.
	 * 
	 * @param id the string id of the type, for use in the config file
	 * @param clazz the class to handle the powerup, the class must have a
	 *            public, no argument constructor and a copy constructor
	 */
	public void registerPowerupType(String id, Class<? extends Powerup> clazz) {
		powerupTypes.put(id, clazz);
	}

}
