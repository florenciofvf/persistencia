package br.com.persist.main;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.UIManager;

import br.com.persist.assistencia.AssistenciaException;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Imagens;
import br.com.persist.assistencia.Preferencias;
import br.com.persist.assistencia.Util;
import br.com.persist.formulario.Formulario;

public class Main {
	private static final Logger LOG = Logger.getGlobal();

	public static void main(String[] args) {
		final String encoding = "UTF-8";
		System.setProperty("sun.jnu.encoding", encoding);
		System.setProperty("stdout.encoding", encoding);
		System.setProperty("stderr.encoding", encoding);
		System.setProperty("file.encoding", encoding);
		System.setProperty("line.separator", "\n");
		String[] opcoes = new String[] { Constantes.CONECTADO, Constantes.DESCONECTADO };
		String opcao = Util.getValorInputDialog(null, opcoes);
		if (opcao == null) {
			return;
		}
		Preferencias.setDesconectado(opcoes[opcoes.length - 1].equals(opcao));
		Preferencias.inicializar();
		installLookAndFeel();
		Preferencias.abrir();
		try {
			Imagens.ini();
		} catch (AssistenciaException ex) {
			Util.mensagem(null, ex.getMessage());
		}
		abrirForm();
	}

	private static void abrirForm() {
		Formulario form = criarFormulario(getGC());
		String string = System.getProperty("form_location_deltaX");
		if (string != null && !string.trim().isEmpty()) {
			Point location = form.getLocation();
			location.x += Integer.parseInt(string.trim());
			form.setLocation(location);
		}
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
		String idForm = Preferencias.getString(Constantes.GC_O_FORMULARIO);
		GraphicsDevice[] gds = ge.getScreenDevices();
		GraphicsConfiguration gcPref = getGC(gds, idPref);
		GraphicsConfiguration gcForm = getGC(gds, idForm);
		return gcPref != null ? gcPref : gcForm;
	}

	private static GraphicsConfiguration getGC(GraphicsDevice[] gds, String id) {
		if (gds == null || id == null) {
			return null;
		}
		for (GraphicsDevice itemGD : gds) {
			GraphicsConfiguration[] gcs = itemGD.getConfigurations();
			if (gcs == null) {
				continue;
			}
			for (GraphicsConfiguration itemGC : gcs) {
				Rectangle bounds = itemGC.getBounds();
				if (bounds != null && valido(id, bounds)) {
					return itemGC;
				}
			}
		}
		return null;
	}

	private static boolean valido(String id, Rectangle bounds) {
		return id.equals(bounds.x + "," + bounds.y);
	}

	public static String getStringGC(GraphicsDevice gd) {
		if (gd == null) {
			return "";
		}
		GraphicsConfiguration[] gcs = gd.getConfigurations();
		if (gcs == null) {
			return "";
		}
		StringBuilder builder = new StringBuilder();
		for (GraphicsConfiguration item : gcs) {
			Rectangle bounds = item.getBounds();
			if (bounds != null) {
				if (builder.length() > 0) {
					builder.append("; ");
				}
				builder.append(bounds.x + "," + bounds.y);
			}
		}
		return builder.toString();
	}
}