package uk.thecodingbadgers.minekart.version;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URL;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;

import uk.thecodingbadgers.minekart.MineKart;
import uk.thecodingbadgers.minekart.version.internal.InternalVersion;

public class NmsHandler {

	private static final String MANIFEST_SECTION = "MineKart";
	private static final String MANIFEST_MAIN_CLASS = "Main-Class";
	private static final String MANIFEST_NMS_VERSION = "Nms-Version";
	private static final String MANIFEST_VERSION = "Version";

	private static final Pattern OBC_FORMAT = Pattern.compile("org.bukkit.craftbukkit.([vR0-9_]+)");
	
	private static final int CURRENT_VERSION = 1;
	
	private static Version version;

	public static Version getNmsHandler() {
		return version;
	}
	
	public static boolean setupNMSHandling() {
		String nmsVersion = getNmsVersion();

		if (nmsVersion != null) {
			version = loadNmsHandler(nmsVersion);
			
			if (version == null) {
				MineKart.getInstance().getLogger().log(Level.WARNING, "Could not load specific nms handling for {0} using internal handling", nmsVersion);
				version = new InternalVersion(); // Fallback on internal version
			}
			
			MineKart.getInstance().getLogger().log(Level.INFO, "Loaded nms handling for version {0}", nmsVersion);
			return true;
		} else {
			return false;
		}
	}

	private static Version loadNmsHandler(String nmsVersion) {
		File[] handlers = MineKart.getNmsFolder().listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.getName().endsWith(".jar");
			}
		});

		File handler = null;
		String mainclass = "";
		
		JarFile current = null;
		
		for (File file : handlers) {
			try {
				current = new JarFile(file);
				Manifest manifest = current.getManifest();
				Attributes attr = manifest.getAttributes(MANIFEST_SECTION);
				
				if (CURRENT_VERSION != Integer.parseInt(attr.getValue(MANIFEST_VERSION))) {
					MineKart.getInstance().getLogger().log(Level.WARNING, "Outdated nms handler {0} (Handler designed for {1} on version {2}", new Object[] { file.getName(), attr.getValue(MANIFEST_VERSION), CURRENT_VERSION });
					continue;
				}
				
				if (nmsVersion.equalsIgnoreCase(attr.getValue(MANIFEST_NMS_VERSION))) {
					handler = file;
					mainclass = attr.getValue(MANIFEST_MAIN_CLASS);
					break;
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (current != null) {
					try {
						current.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		
		if (handler == null) {
			return null;
		}
		
		try {
			@SuppressWarnings("resource")
			NmsHandlerClassLoader loader = new NmsHandlerClassLoader(new URL[] { handler.toURI().toURL() }, NmsHandler.class.getClassLoader());
			Class<?> clazz = loader.loadClass(mainclass);
			
			Class<? extends Version> main = clazz.asSubclass(Version.class);
			return main.newInstance();
		} catch (Throwable e) { // Exception handling
			String message = "An unexpected exception occured whilst trying to setup version handler for " + nmsVersion;
			
			if (e instanceof InstantiationException) {
				e = e.getCause();
			} 
			
			if (e instanceof ClassNotFoundException) {
				message = String.format("Could not find class %2$s for version %2$s", e.getMessage(), nmsVersion);
			} else if (e instanceof ClassCastException) {
				message = String.format("Main class for version %1$s (%2$s) is not a subclass of Version", nmsVersion, mainclass);
				e = null;
			}
			
			MineKart.getInstance().getLogger().log(Level.SEVERE, message);
			if (e != null) MineKart.getInstance().getLogger().log(Level.SEVERE, "Exception: ", e);
			
			return null; // Fallback on internal handling
		}
	}

	private static String getNmsVersion() {
		String obcPackage = Bukkit.getServer().getClass().getPackage().getName();
		Matcher matcher = OBC_FORMAT.matcher(obcPackage);

		if (!matcher.matches()) {
			MineKart.getInstance().getLogger().log(Level.SEVERE, "Server class did not match Craftbukkit package, are you running CraftBukkit?");
			MineKart.getInstance().getLogger().log(Level.SEVERE, "Package: {0}", obcPackage);
			return null;
		}
		
		return matcher.group(1);
	}

	public static void cleanup() {
		version = null;
	}
}
