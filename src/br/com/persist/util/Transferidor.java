package br.com.persist.util;

import java.awt.Dimension;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.Objects;

import br.com.persist.banco.Conexao;
import br.com.persist.objeto.Objeto;

public class Transferidor implements Transferable {
	public static final DataFlavor flavor = new DataFlavor(Transferidor.class, "Transferidor");
	private static final DataFlavor[] flavors = { flavor };
	private final Dimension dimension;
	private final Conexao conexao;
	private final String apelido;
	private final Objeto objeto;

	public Transferidor(Objeto objeto, Conexao conexao, Dimension dimension, String apelido) {
		Objects.requireNonNull(dimension);
		Objects.requireNonNull(conexao);
		Objects.requireNonNull(objeto);
		this.dimension = dimension;
		this.conexao = conexao;
		this.apelido = apelido;
		this.objeto = objeto.isCopiarDestacado() ? objeto.clonar() : objeto;
	}

	public Dimension getDimension() {
		return dimension;
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
			return new Object[] { objeto, conexao, dimension, apelido };
		}

		throw new UnsupportedFlavorException(flavor);
	}
}