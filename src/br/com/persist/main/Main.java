package br.com.persist.main;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.UIManager;

import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Imagens;
import br.com.persist.assistencia.Preferencias;
import br.com.persist.formulario.Formulario;

public class Main {
	private static final Logger LOG = Logger.getGlobal();

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		} catch (Exception e) {
			LOG.log(Level.INFO, Constantes.INFO, e);
		}
		Preferencias.inicializar();
		URL[] urLs = getURLs();
		Preferencias.abrir();
		Imagens.ini();
		for (URL url : urLs) {
			addURL(url);
		}
		GraphicsConfiguration gc = getGraphicsConfiguration();
		Formulario formulario = gc == null ? new Formulario() : new Formulario(gc);
		formulario.checarPreferenciasLarguraAltura();
		formulario.setVisible(true);
	}

	private static GraphicsConfiguration getGraphicsConfiguration() {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		String id = Preferencias.getString(Constantes.GC);
		GraphicsDevice[] gs = ge.getScreenDevices();
		GraphicsDevice device = null;
		if (gs != null && id != null) {
			for (GraphicsDevice gd : gs) {
				if (id.equals(gd.getIDstring())) {
					device = gd;
				}
			}
		}
		if (device != null) {
			GraphicsConfiguration[] gcs = device.getConfigurations();
			if (gcs != null && gcs.length > 0) {
				return gcs[0];
			}
		}
		return null;
	}

	private static void addURL(URL url) {
		try {
			URLClassLoader classLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
			Class<?> classe = URLClassLoader.class;
			Method method = classe.getDeclaredMethod("addURL", URL.class);
			method.setAccessible(true);
			method.invoke(classLoader, url);
		} catch (Exception e) {
			LOG.log(Level.SEVERE, Constantes.ERRO, e);
		}
	}

	private static URL[] getURLs() {
		File[] files = new File("libs").listFiles();
		List<URL> urls = new ArrayList<>();
		addURL(files, urls);
		return urls.toArray(new URL[urls.size()]);
	}

	private static void addURL(File[] files, List<URL> urls) {
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
			LOG.log(Level.SEVERE, Constantes.ERRO, e);
		}
	}
}