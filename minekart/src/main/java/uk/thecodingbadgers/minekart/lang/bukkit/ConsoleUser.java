package uk.thecodingbadgers.minekart.lang.bukkit;

import uk.thecodingbadgers.minekart.MineKart;
import uk.thecodingbadgers.minekart.lang.Lang;
import uk.thecodingbadgers.minekart.lang.LangUser;
import uk.thecodingbadgers.minekart.lang.LangUtils;

/**
 * Created by James on 28/04/2014.
 */
public class ConsoleUser implements LangUser { // TODO do we really need this, can it not just be a bukkit wrapper
    @Override
    public void sendMessage(String message) {
        MineKart.getInstance().getLogger().info(message);
    }

    @Override
    public Lang getLanguage() {
        return LangUtils.getLang();
    }
}
