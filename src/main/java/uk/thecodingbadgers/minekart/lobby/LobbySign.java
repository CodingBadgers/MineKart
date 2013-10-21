package uk.thecodingbadgers.minekart.lobby;

import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;

import uk.thecodingbadgers.minekart.MineKart;
import uk.thecodingbadgers.minekart.race.Race;
import uk.thecodingbadgers.minekart.racecourse.Racecourse;

public class LobbySign {

    private Block block;
    private Racecourse course;
    
    public LobbySign(Block location, Racecourse course) {
        this.block = location;
        this.course = course;
    }
    
    public void update() {
        Sign sign = getSign();
        
        if (sign == null) {
            return;
        }
        
        int i = 0;
        
        for (String string : getSignContent()) {
            sign.setLine(i, string);
            i++;
        }
        
        sign.update();
    }
    
    public String[] getSignContent() {
        Race race = course.getRace();
        
        String[] lines = new String[4];
        lines[0] = ChatColor.DARK_GREEN + "[MineKart]";
        lines[1] = ChatColor.GREEN + course.getName();
        lines[2] = ChatColor.GREEN + "" + race.getJockeys().size() + "/" + course.getMultiWarp("spawn").size();
        lines[3] = ChatColor.GREEN + race.getState().toString();
        return lines;
    }
    
    public Sign getSign() {
        BlockState state = block.getState();
        
        if (state instanceof Sign) {
            return (Sign) state;
        }
        
        MineKart.getInstance().getLogger().log(Level.SEVERE, "Lobby sign is not a sign");
        return null;
    }
}
