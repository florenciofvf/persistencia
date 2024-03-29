package br.com.persist.painel;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.com.persist.componente.Panel;
import br.com.persist.marca.XMLUtil;

public abstract class Transferivel extends Panel implements Transferable {
	private static final long serialVersionUID = -2395376493141225954L;
	public static final int ACAO_VALIDA = DnDConstants.ACTION_MOVE;
	public static final DataFlavor flavor = createDataFlavor();
	private static final DataFlavor[] flavors = { flavor };
	private static final Logger LOG = Logger.getGlobal();
	public static final String RENOMEAR = "RENOMEAR";
	private transient Setor setor;
	private String title;
	private String hint;
	private int index;

	public static DataFlavor createDataFlavor() {
		try {
			return new DataFlavor(
					DataFlavor.javaJVMLocalObjectMimeType + ";class=\"" + Transferivel.class.getName() + "\"");
		} catch (ClassNotFoundException e) {
			return null;
		}
	}

	public static boolean acaoValida(int acao) {
		return (acao & ACAO_VALIDA) != 0;
	}

	@Override
	public DataFlavor[] getTransferDataFlavors() {
		return flavors;
	}

	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		return Transferivel.flavor.equals(flavor);
	}

	@Override
	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
		if (Transferivel.flavor.equals(flavor)) {
			return this;
		}
		throw new UnsupportedFlavorException(flavor);
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getHint() {
		return hint;
	}

	public void setHint(String hint) {
		this.hint = hint;
	}

	public Setor getSetor() {
		return setor;
	}

	public void setSetor(Setor setor) {
		this.setor = setor;
	}

	@Override
	public String toString() {
		return title;
	}

	public void processar(Fichario fichario, int indice, Map<String, Object> map) {
	}

	public void salvar(XMLUtil util) {
		util.abrirTag("transferivel");
		String file = getStringFile();
		if (file != null) {
			util.atributo("file", file);
		}
		util.fecharTag(-1);
	}

	public boolean associadoA(File file) {
		if (file != null) {
			LOG.log(Level.FINEST, file.getAbsolutePath());
		}
		return false;
	}

	public String getStringFile() {
		return null;
	}

	public File getFile() {
		return null;
	}
}