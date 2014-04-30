package uk.thecodingbadgers.minekart.lang.bukkit;

import org.bukkit.command.CommandSender;
import uk.thecodingbadgers.minekart.lang.Lang;
import uk.thecodingbadgers.minekart.lang.LangUser;
import uk.thecodingbadgers.minekart.lang.LangUtils;

/**
 * Created by James on 28/04/2014.
 */
public class BukkitWrapper implements LangUser {
    private final CommandSender sender;

    public BukkitWrapper(CommandSender sender) {
        this.sender = sender;
    }

    @Override
    public void sendMessage(String message) {
        sender.sendMessage(message);
    }

    @Override
    public Lang getLanguage() {
        return LangUtils.getLang();
    }
}
