package uk.thecodingbadgers.minekart.lobby;

import java.util.HashSet;
import java.util.Set;

public class LobbySignManager {

    private static Set<LobbySign> signs = new HashSet<LobbySign>();

    public static void loadSigns() {
        
    }
    
    public static void addSign(LobbySign location) {
        signs.add(location);
    }
    
    public static void updateSigns() {
        for (LobbySign sign : signs ) {
            sign.update();
        }
    }
}
