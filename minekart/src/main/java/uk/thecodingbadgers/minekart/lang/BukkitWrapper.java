package uk.thecodingbadgers.minekart.lang;

import org.bukkit.command.CommandSender;

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
