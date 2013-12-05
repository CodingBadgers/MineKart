package uk.thecodingbadgers.minekart.jockey;

import java.util.List;
import java.util.Random;

import org.bukkit.Color;

public class JockeySpecialData {

	private String username;
	private List<String> horsenames = null;
	private Color colour = null;

	public String getUsername() {
		return username;
	}

	public Color getJockeyColour() {
		return colour;
	}

	public String getHorseName() {
		if (horsenames == null) {
			return null;
		}

		return horsenames.get(new Random().nextInt(horsenames.size()));
	}

	public boolean hasCustomColour() {
		return colour != null;
	}

	public boolean hasCustomName() {
		return horsenames != null;
	}
	
	public String toString() {
		return getClass().getSimpleName() + "{Username:" + username + ";Names:" + horsenames + ";Colour:" + colour + "}";
	}
}
