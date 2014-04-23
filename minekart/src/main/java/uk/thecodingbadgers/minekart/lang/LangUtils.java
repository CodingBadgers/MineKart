package uk.thecodingbadgers.minekart.lang;

import java.io.File;
import java.io.FileFilter;
import java.util.Map;

import org.bukkit.command.CommandSender;

import uk.thecodingbadgers.minekart.MineKart;

import com.google.common.collect.Maps;

public class LangUtils {

	private static final String MESSAGE_FORMAT_KEY = "message.format";
	
	private static Map<String, Lang> langs = Maps.newHashMap();
	private static Lang defaultLang = null;
	
	public static Lang getLang() {
		return defaultLang;
	}
	
	public static Lang getLang(String format) {
		return langs.get(format);
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
	
	public static void sendMessage(Messageable sender, String key, Object... args) {
		sender.sendMessage(formatMessage(sender.getLanguage(), sender.getLanguage().getTranslation(key, args)));
	}

	public static void sendMessage(CommandSender sender, String message) {
		sender.sendMessage(formatMessage(getLang(), message));
	}

	public static void sendMessage(CommandSender sender, Lang lang, String key, Object... args) {
		sender.sendMessage(formatMessage(lang, lang.getTranslation(key, args)));
	}

	public static String formatMessage(String message) {
		return formatMessage(getLang(), message);
	}
	
	public static String formatMessage(Lang lang, String message) {
		return lang.getTranslation(MESSAGE_FORMAT_KEY, MineKart.getInstance().getDescription().getName(), message);
	}
}
