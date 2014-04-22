package uk.thecodingbadgers.minekart.jockey;

import java.util.List;
import java.util.Random;

import static org.apache.commons.lang.builder.HashCodeBuilder.reflectionHashCode;
import static org.apache.commons.lang.builder.ToStringBuilder.reflectionToString;

import org.bukkit.Color;

public class JockeySpecialData implements Cloneable {

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
		if (hasCustomName()) {
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

	@Override
	public int hashCode() {
		return reflectionHashCode(this);
	}
	
	@Override
	public String toString() {
		return reflectionToString(this);
	}
	
	@Override
	public JockeySpecialData clone() {
		JockeySpecialData data = new JockeySpecialData();
		data.username = username;
		data.horsenames = horsenames;
		data.colour = colour;
		return data;
	}
}
