package uk.thecodingbadgers.minekart.version;

import java.net.URL;
import java.net.URLClassLoader;

public class NmsHandlerClassLoader extends URLClassLoader {

	public NmsHandlerClassLoader(URL[] urls, ClassLoader parent) {
		super(urls, parent);
	}

	@Override
	public void addURL(URL url) {
		super.addURL(url);
	}

}
