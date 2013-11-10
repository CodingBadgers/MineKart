package uk.thecodingbadgers.minekart.powerup;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.Validate;

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
	 * <p>
	 * Along with abstract methods a powerup must have a public no argument 
	 * constructor and a public copy constructor.
	 * 
	 * @param id the string id of the type, for use in the config file
	 * @param clazz the class to handle the powerup
	 * @throws IllegalArgumentException if powerup constraints are not met
	 */
	public void registerPowerupType(String id, Class<? extends Powerup> clazz) throws IllegalArgumentException {
		checkForConstructor(clazz, "A powerup must have a valid public no arguement constructor");
		checkForConstructor(clazz, "A powerup must have a valid public copy constructor constructor", clazz);
		
		powerupTypes.put(id, clazz);
	}
	
	private void checkForConstructor(Class<?> clazz, String error, Class<?>... arguements) {
		try {
			Validate.isTrue(clazz.getConstructor(arguements) != null, error);
		} catch (Exception ex) {
			throw new IllegalArgumentException(error);
		}
	}

}
