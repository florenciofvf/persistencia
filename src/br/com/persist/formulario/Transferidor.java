package br.com.persist.formulario;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.Objects;

import br.com.persist.Objeto;
import br.com.persist.banco.Conexao;

public class Transferidor implements Transferable {
	public static final DataFlavor flavor = new DataFlavor(Transferidor.class, "Transferidor");
	private static final DataFlavor[] flavors = { flavor };
	private final Conexao conexao;
	private final Objeto objeto;

	public Transferidor(Objeto objeto, Conexao conexao) {
		Objects.requireNonNull(conexao);
		Objects.requireNonNull(objeto);
		this.conexao = conexao;
		this.objeto = objeto;
	}

	public Conexao getConexao() {
		return conexao;
	}

	public Objeto getObjeto() {
		return objeto;
	}

	@Override
	public DataFlavor[] getTransferDataFlavors() {
		return flavors;
	}

	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		return Transferidor.flavor.equals(flavor);
	}

	@Override
	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
		if (Transferidor.flavor.equals(flavor)) {
			return new Object[] { objeto, conexao };
		}

		throw new UnsupportedFlavorException(flavor);
	}
}