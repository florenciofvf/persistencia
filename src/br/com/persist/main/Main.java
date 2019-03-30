package br.com.persist.main;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import br.com.persist.principal.Formulario;
import br.com.persist.util.Imagens;
import br.com.persist.util.Preferencias;

public class Main {
	public static void main(String[] args) throws Exception {
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

	public static void addURL(URL url) throws Exception {
		URLClassLoader classLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
		Class<?> classe = URLClassLoader.class;

		Method method = classe.getDeclaredMethod("addURL", URL.class);
		method.setAccessible(true);
		method.invoke(classLoader, url);
	}

	private static URL[] getURLs() throws Exception {
		File[] files = new File("libs").listFiles();
		List<URL> urls = new ArrayList<>();

		if (files != null) {
			for (File f : files) {
				String s = f.getName().toLowerCase();

				if (s.endsWith(".jar")) {
					URI uri = f.toURI();
					urls.add(uri.toURL());
				}
			}
		}

		return urls.toArray(new URL[urls.size()]);
	}
}