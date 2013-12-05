package uk.thecodingbadgers.minekart.jockey;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class JockeyDataManager {

	private static final Random random = new Random();
	private List<JockeySpecialData> special;
	private List<String> horsenames;
	
	public JockeySpecialData getJockeyData(Jockey jockey) {
		Iterator<JockeySpecialData> itr = special.iterator();
		while(itr.hasNext()) {
			JockeySpecialData cur = itr.next();
			if (cur.getUsername().equalsIgnoreCase(jockey.getPlayer().getName())) {
				return cur;
			}
		}
		return null;
	}
	
	public String getRandomHorsename() {
		return horsenames.get(random.nextInt(horsenames.size()));
	}
	
	public String toString() {
		return getClass().getSimpleName() + "{Special:" + special + ";Names:" + horsenames + "}";
	}
}
