package br.com.persist.main;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
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
import br.com.persist.assistencia.Util;
import br.com.persist.formulario.Formulario;

public class Main {
	private static final Logger LOG = Logger.getGlobal();

	public static void main(String[] args) {
		String[] opcoes = new String[] { "CONECTADO", "DESCONECTADO" };
		String opcao = Util.getValorInputDialog(null, opcoes);
		if (opcao == null) {
			return;
		}
		Preferencias.setDesconectado(opcoes[opcoes.length - 1].equals(opcao));
		Preferencias.inicializar();
		installLookAndFeel();
		Preferencias.abrir();
		addURLs(getURLs());
		Imagens.ini();
		abrirForm();
	}

	private static void abrirForm() {
		Formulario form = criarFormulario(getGC());
		form.setVisible(true);
	}

	private static Formulario criarFormulario(GraphicsConfiguration gc) {
		return gc == null ? new Formulario() : new Formulario(gc);
	}

	private static void installLookAndFeel() {
		try {
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		} catch (Exception e) {
			LOG.log(Level.INFO, Constantes.INFO, e);
		}
	}

	private static GraphicsConfiguration getGC() {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		String idPref = Preferencias.getString(Constantes.GC_PREFERENCIAL);
		String id = Preferencias.getString(Constantes.GC);
		GraphicsDevice[] gs = ge.getScreenDevices();
		GraphicsDevice devicePref = getDevice(idPref, gs);
		GraphicsDevice device = getDevice(id, gs);
		return devicePref != null ? getGC(devicePref) : getGC(device);
	}

	private static GraphicsDevice getDevice(String id, GraphicsDevice[] gs) {
		GraphicsDevice device = null;
		if (gs != null && id != null) {
			for (GraphicsDevice gd : gs) {
				if (id.equals(gd.getIDstring())) {
					device = gd;
				}
			}
		}
		return device;
	}

	private static GraphicsConfiguration getGC(GraphicsDevice device) {
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

	private static void addURLs(URL[] urLs) {
		for (URL url : urLs) {
			addURL(url);
		}
	}

	private static void addURL(File[] files, List<URL> urls) {
		try {
			if (files != null) {
				addURLs(files, urls);
			}
		} catch (Exception e) {
			LOG.log(Level.SEVERE, Constantes.ERRO, e);
		}
	}

	private static void addURLs(File[] files, List<URL> urls) throws MalformedURLException {
		for (File f : files) {
			String s = f.getName().toLowerCase();
			if (s.endsWith(".jar")) {
				URI uri = f.toURI();
				urls.add(uri.toURL());
			}
		}
	}
}