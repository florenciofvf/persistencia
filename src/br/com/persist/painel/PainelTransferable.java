package br.com.persist.painel;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import br.com.persist.componente.Panel;

public class PainelTransferable extends Panel implements Transferable {
	private static final long serialVersionUID = -2395376493141225954L;
	public static final DataFlavor flavor = createDataFlavor();
	private static final DataFlavor[] flavors = { flavor };
	private String title;
	private int index;

	public static DataFlavor createDataFlavor() {
		try {
			return new DataFlavor(
					DataFlavor.javaJVMLocalObjectMimeType + ";class=\"" + PainelTransferable.class.getName() + "\"");
		} catch (ClassNotFoundException e) {
			return null;
		}
	}

	@Override
	public DataFlavor[] getTransferDataFlavors() {
		return flavors;
	}

	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		return PainelTransferable.flavor.equals(flavor);
	}

	@Override
	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
		if (PainelTransferable.flavor.equals(flavor)) {
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
}