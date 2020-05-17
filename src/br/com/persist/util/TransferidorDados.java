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

import javax.swing.plaf.UIResource;

public class TransferidorDados implements Transferable, UIResource {
	private static DataFlavor[] flavors = new DataFlavor[8];
	private static final Logger LOG = Logger.getGlobal();
	private final String tabular;
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
			flavors[indice++] = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType + ";class=java.lang.String");
			flavors[indice++] = DataFlavor.stringFlavor;
		} catch (ClassNotFoundException cle) {
			LOG.log(Level.SEVERE, Constantes.ERRO, cle);
		}
	}

	public TransferidorDados(String html, String tabular) {
		this.tabular = tabular;
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
				return html;
			} else if (Reader.class.equals(flavor.getRepresentationClass())) {
				return new StringReader(html);
			} else if (InputStream.class.equals(flavor.getRepresentationClass())) {
				return createInputStream(flavor, html);
			}

		} else if (isPlainFlavor(flavor)) {
			if (String.class.equals(flavor.getRepresentationClass())) {
				return tabular;
			} else if (Reader.class.equals(flavor.getRepresentationClass())) {
				return new StringReader(tabular);
			} else if (InputStream.class.equals(flavor.getRepresentationClass())) {
				return createInputStream(flavor, tabular);
			}

		} else if (isStringFlavor(flavor)) {
			return tabular;
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
		for (int i = 3; i < 6; i++) {
			if (flavors[i].equals(flavor)) {
				return true;
			}
		}

		return false;
	}

	private boolean isStringFlavor(DataFlavor flavor) {
		for (int i = 6; i < flavors.length; i++) {
			if (flavors[i].equals(flavor)) {
				return true;
			}
		}

		return false;
	}
}