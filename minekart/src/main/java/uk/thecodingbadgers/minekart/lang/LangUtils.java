package uk.thecodingbadgers.minekart.lang;

import java.io.File;
import java.io.FileFilter;
import java.util.Map;
import java.util.UUID;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import uk.thecodingbadgers.minekart.MineKart;

import com.google.common.collect.Maps;

public class LangUtils {

	private static final String MESSAGE_FORMAT_KEY = "message.format";
	
	private static Map<String, Lang> langs = Maps.newHashMap();
	private static Map<UUID, LangUser> players = Maps.newHashMap();
    private static LangUser consoleSender = new ConsoleUser();
	private static Lang defaultLang = null;

    public static LangUser getConsoleSender() {
        return consoleSender;
    }

    public static Lang getLang() {
		return defaultLang;
	}
	
	public static Lang getLang(String format) {
		return langs.get(format);
	}

    public static Lang getLang(CommandSender sender) {
        LangUser user = null;

        if (sender instanceof Player) {
            user = players.get(((Player) sender).getUniqueId());
        } else if (sender instanceof ConsoleCommandSender) {
            user = getConsoleSender();
        } else {
            user = new BukkitWrapper(sender);
        }

        return user.getLanguage();
    }

	public static void setupLanguages() {
		File[] files = MineKart.getLangFolder().listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.getName().endsWith(".lang");
			}
		});
		
		for (File file : files) {
			langs.put(file.getName().substring(0, file.getName().length() - 5), new Lang(file));
		}
		
		defaultLang = langs.get("en_GB"); // TODO load from config
	}
	
	public static void sendMessage(LangUser sender, String key, Object... args) {
		sender.sendMessage(formatMessage(sender.getLanguage(), sender.getLanguage().getTranslation(key, args)));
	}

	public static void sendMessage(CommandSender sender, String key, Object...args) {
        LangUser user = null;

        if (sender instanceof Player) {
            user = players.get(((Player) sender).getUniqueId());
        } else if (sender instanceof ConsoleCommandSender) {
            user = getConsoleSender();
        } else {
            user = new BukkitWrapper(sender);
        }

		sendMessage(user, key, args);
	}

	@Deprecated 
	public static void sendMessage(Player sender, Lang lang, String key, Object... args) {
		sendMessage(players.get(sender.getUniqueId()), key, args);
	}

	public static String formatMessage(String message) {
		return formatMessage(getLang(), message);
	}
	
	public static String formatMessage(Lang lang, String message) {
		return lang.getTranslation(MESSAGE_FORMAT_KEY, MineKart.getInstance().getDescription().getName(), message);
	}

}
