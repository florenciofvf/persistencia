package br.com.persist.plugins.objeto.internal;

import java.awt.Dimension;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.Objects;

import br.com.persist.plugins.conexao.Conexao;
import br.com.persist.plugins.objeto.Objeto;
import br.com.persist.plugins.objeto.ObjetoSuperficie;

public class InternalTransferidor implements Transferable {
	public static final DataFlavor flavor = new DataFlavor(InternalTransferidor.class, "Transferidor");
	private static final DataFlavor[] flavors = { flavor };
	private final Dimension dimension;
	private final Conexao conexao;
	private final String apelido;
	private final Objeto objeto;

	public static final byte ARRAY_INDICE_OBJ = 0;
	public static final byte ARRAY_INDICE_CON = 1;
	public static final byte ARRAY_INDICE_DIM = 2;
	public static final byte ARRAY_INDICE_APE = 3;

	public InternalTransferidor(Objeto objeto, Conexao conexao, Dimension dimension, String apelido) {
		Objects.requireNonNull(dimension);
		Objects.requireNonNull(conexao);
		Objects.requireNonNull(objeto);
		this.dimension = dimension;
		this.conexao = conexao;
		this.apelido = apelido;
		this.objeto = objeto.isCopiarDestacado() ? objeto.clonar() : objeto;
	}

	public static Object[] criarArray(Conexao conexao, Objeto objeto, Dimension dimension, String apelido) {
		ObjetoSuperficie.setComplemento(conexao, objeto);
		return new Object[] { objeto, conexao, dimension, apelido };
	}

	public static Object[] criarArray(Conexao conexao, Objeto objeto, String apelido) {
		return criarArray(conexao, objeto, new Dimension(400, 250), apelido);
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
		return InternalTransferidor.flavor.equals(flavor);
	}

	@Override
	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
		if (InternalTransferidor.flavor.equals(flavor)) {
			return new Object[] { objeto, conexao, dimension, apelido };
		}

		throw new UnsupportedFlavorException(flavor);
	}
}