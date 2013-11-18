package uk.thecodingbadgers.minekart.racecourse;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.Validate;

public class RacecourseTypeRegistry {

	private Map<String, Class<? extends Racecourse>> racecourseTypes;

	/**
	 * Instantiates a new racecourse registry.
	 */
	public RacecourseTypeRegistry() {
		racecourseTypes = new HashMap<String, Class<? extends Racecourse>>();
	}

	/**
	 * Creates a racecourse by its string id.
	 * 
	 * @param racecoursetype the string id
	 * @return the new racecourse instance, or null if the id is not registered
	 * @throws NullPointerException if the string id is null
	 */
	public Racecourse createRacecourse(String racecoursetype) throws NullPointerException {
		Validate.notNull(racecoursetype, "Racecourse type id cannot be null");
		
		try {
			Class<? extends Racecourse> clazz = racecourseTypes.get(racecoursetype);

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
	 * Register a new custom racecourse type.
	 * 
	 * @param id the string id of the type, for use in the config file
	 * @param clazz the class to handle the racecourse
	 * @throws NullPointerException if either argument is null
	 */
	public void registerRacecourseType(String id, Class<? extends Racecourse> clazz) throws NullPointerException {		
		Validate.notNull(id, "Racecourse type id cannot be null");
		Validate.notNull(clazz, "Racecourse type clazz cannot be null");
		
		racecourseTypes.put(id, clazz);
	}

}
