package uk.thecodingbadgers.minekart.world;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;

public class BlockDelagatorFactory {

	private static final Map<String, Class<? extends BlockChangeDelagator>> delagators = new HashMap<String, Class<? extends BlockChangeDelagator>>();
	
	static {
		delagators.put("fake", FakeBlockChangeDelagator.class);
		delagators.put("bukkit", BukkitBlockChangeDelagator.class);
	}
	
	public static BlockChangeDelagator createChangeDelagator(String type, final Player player) {
		try {
			Class<? extends BlockChangeDelagator> clazz = delagators.get(type);
			
			if (clazz == null) {
				return null;
			}
			
			Constructor<? extends BlockChangeDelagator> ctor = clazz.getConstructor(Player.class);
			return ctor.newInstance(player);
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}
}
