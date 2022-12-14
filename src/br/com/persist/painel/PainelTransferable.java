package br.com.persist.painel;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import javax.swing.JPanel;

public class PainelTransferable extends JPanel implements Transferable {
	public static final DataFlavor flavor = new DataFlavor(PainelTransferable.class, "PainelTransferable");
	private static final long serialVersionUID = -2395376493141225954L;
	private static final DataFlavor[] flavors = { flavor };

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
}