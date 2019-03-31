package br.com.persist.main;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.com.persist.principal.Formulario;
import br.com.persist.util.Imagens;
import br.com.persist.util.Preferencias;

public class Main {
	private static final Logger LOG = Logger.getGlobal();

	public static void main(String[] args) {
		Preferencias.abrir();

		URL[] urLs = getURLs();

		for (URL url : urLs) {
			addURL(url);
		}

		Imagens.ini();
		Formulario formulario = new Formulario();
		formulario.setLocationRelativeTo(null);
		formulario.setVisible(true);
	}

	public static void addURL(URL url) {
		try {
			URLClassLoader classLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
			Class<?> classe = URLClassLoader.class;

			Method method = classe.getDeclaredMethod("addURL", URL.class);
			method.setAccessible(true);
			method.invoke(classLoader, url);
		} catch (Exception e) {
			LOG.log(Level.SEVERE, "ERRO", e);
		}
	}

	private static URL[] getURLs() {
		File[] files = new File("libs").listFiles();
		List<URL> urls = new ArrayList<>();

		try {
			if (files != null) {
				for (File f : files) {
					String s = f.getName().toLowerCase();

					if (s.endsWith(".jar")) {
						URI uri = f.toURI();
						urls.add(uri.toURL());
					}
				}
			}
		} catch (Exception e) {
			LOG.log(Level.SEVERE, "ERRO", e);
		}

		return urls.toArray(new URL[urls.size()]);
	}
}