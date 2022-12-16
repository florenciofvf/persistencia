package br.com.persist.painel;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.io.IOException;

import br.com.persist.componente.Panel;

public class Transferivel extends Panel implements Transferable {
	private static final long serialVersionUID = -2395376493141225954L;
	public static final int ACAO_VALIDA = DnDConstants.ACTION_MOVE;
	public static final DataFlavor flavor = createDataFlavor();
	private static final DataFlavor[] flavors = { flavor };
	private transient Setor setor;
	private String title;
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
}