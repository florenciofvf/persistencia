package br.com.persist.util;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TransferidorHtml implements Transferable {
	private static DataFlavor[] flavors = new DataFlavor[6];
	private static final Logger LOG = Logger.getGlobal();
	private final String texto;
	private final String html;

	static {
		try {
			int indice = 0;
			flavors[indice++] = new DataFlavor("text/html;class=java.lang.String");
			flavors[indice++] = new DataFlavor("text/html;class=java.io.Reader");
			flavors[indice++] = new DataFlavor("text/html;charset=unicode;class=java.io.InputStream");
			flavors[indice++] = new DataFlavor("text/plain;class=java.lang.String");
			flavors[indice++] = new DataFlavor("text/plain;class=java.io.Reader");
			flavors[indice++] = new DataFlavor("text/plain;charset=unicode;class=java.io.InputStream");
		} catch (ClassNotFoundException cle) {
			LOG.log(Level.SEVERE, Constantes.ERRO, cle);
		}
	}

	public TransferidorHtml(String html, String texto) {
		this.texto = texto;
		this.html = html;
	}

	public DataFlavor[] getTransferDataFlavors() {
		return flavors;
	}

	public boolean isDataFlavorSupported(DataFlavor flavor) {
		for (DataFlavor df : flavors) {
			if (df.equals(flavor)) {
				return true;
			}
		}

		return false;
	}

	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
		if (isHTMLFlavor(flavor)) {
			if (String.class.equals(flavor.getRepresentationClass())) {
				return texto;
			} else if (Reader.class.equals(flavor.getRepresentationClass())) {
				return new StringReader(texto);
			} else if (InputStream.class.equals(flavor.getRepresentationClass())) {
				return createInputStream(flavor, texto);
			}

		} else if (isPlainFlavor(flavor)) {
			if (String.class.equals(flavor.getRepresentationClass())) {
				return html;
			} else if (Reader.class.equals(flavor.getRepresentationClass())) {
				return new StringReader(html);
			} else if (InputStream.class.equals(flavor.getRepresentationClass())) {
				return createInputStream(flavor, html);
			}
		}

		throw new UnsupportedFlavorException(flavor);
	}

	private InputStream createInputStream(DataFlavor flavor, String string)
			throws IOException, UnsupportedFlavorException {
		String cs = flavor.getParameter("charset");

		if (cs == null) {
			throw new UnsupportedFlavorException(flavor);
		}

		return new ByteArrayInputStream(string.getBytes(cs));
	}

	private boolean isHTMLFlavor(DataFlavor flavor) {
		for (int i = 0; i < 3; i++) {
			if (flavors[i].equals(flavor)) {
				return true;
			}
		}

		return false;
	}

	private boolean isPlainFlavor(DataFlavor flavor) {
		for (int i = 3; i < flavors.length; i++) {
			if (flavors[i].equals(flavor)) {
				return true;
			}
		}

		return false;
	}
}