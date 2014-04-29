package uk.thecodingbadgers.minekart.lang;

import uk.thecodingbadgers.minekart.MineKart;

/**
 * Created by James on 28/04/2014.
 */
public class ConsoleUser implements LangUser {
    @Override
    public void sendMessage(String message) {
        MineKart.getInstance().getLogger().info(message);
    }

    @Override
    public Lang getLanguage() {
        return LangUtils.getLang();
    }
}
