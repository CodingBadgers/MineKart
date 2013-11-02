package uk.thecodingbadgers.minekart.util;

public class RaceHelper {
	
	/**
	 * Convert a value into 1st, 2nd, 3rd ect..
	 * @param value The value to convert
	 * @return The string representation of the value
	 */
	public static String ordinalNo(int value) {
		
        int hunRem = value % 100;
        int tenRem = value % 10;
        if (hunRem - tenRem == 10) {
        	return value + "th";
        }
        
        switch (tenRem) {
	        case 1:
	        	return value + "st";
	        case 2:
                return value + "nd";
	        case 3:
                return value + "rd";
	        default:
                return value + "th";
        }
	}

}
