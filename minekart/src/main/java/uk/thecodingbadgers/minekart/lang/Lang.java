package uk.thecodingbadgers.minekart.lang;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import uk.thecodingbadgers.minekart.MineKart;

import com.google.common.collect.Maps;

public class Lang {

	private static final Logger LOG = MineKart.getInstance().getLogger();
	private static final Pattern LANG_ENTRY = Pattern.compile("([^=]+)=(.+)");
	private Map<String, String> langKeys = Maps.newHashMap();
	
	public Lang(File file) {
		loadFile(file);
	}

	private void loadFile(File file) {
		BufferedReader reader = null;
		
		try {
			reader = new BufferedReader(new FileReader(file));
			String line = "";
			
			while((line = reader.readLine()) != null) {
				if (line.length() == 0 || line.startsWith("#") || line.startsWith("//")) continue; // ignore comments or empty lines
				
				Matcher matcher = LANG_ENTRY.matcher(line);
				
				if (!matcher.matches()) {
					LOG.log(Level.WARNING, "A entry in the language file {0} is malformed ({1})", new Object[] { file.getName(), line });
					continue;
				}
				
				langKeys.put(matcher.group(0), matcher.group(1));
			}
		
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		}
	}
	
	public String getTranslation(String key, Object... args) {
		return String.format(langKeys.get(key), args);
	}
	
	public void sendMessage(LangUser player, String key, Object... args) {
		player.sendMessage(getTranslation(key, args));
	}
}
