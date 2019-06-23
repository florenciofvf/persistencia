package br.com.persist;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import br.com.persist.util.Constantes;
import br.com.persist.util.Util;

public class Metadado implements Transferable {
	public static final DataFlavor flavor = new DataFlavor(Metadado.class, "Metadado");
	private static final DataFlavor[] flavors = { flavor };
	private final List<Metadado> filhos;
	private final String descricao;
	private boolean tabela;
	private Metadado pai;

	public Metadado(String descricao) {
		if (Util.estaVazio(descricao)) {
			throw new IllegalArgumentException();
		}
		this.descricao = descricao;
		filhos = new ArrayList<>();
	}

	public int getIndice(Metadado metadado) {
		return filhos.indexOf(metadado);
	}

	public void add(Metadado metadado) {
		filhos.add(metadado);
		metadado.pai = this;
	}

	public String getDescricao() {
		return descricao;
	}

	public int getTotal() {
		return filhos.size();
	}

	public boolean estaVazio() {
		return filhos.isEmpty();
	}

	public Metadado getMetadado(int index) {
		return filhos.get(index);
	}

	public Metadado getPai() {
		return pai;
	}

	public boolean isTabela() {
		return tabela;
	}

	public void setTabela(boolean tabela) {
		this.tabela = tabela;
	}

	@Override
	public String toString() {
		return descricao;
	}

	@Override
	public DataFlavor[] getTransferDataFlavors() {
		return flavors;
	}

	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		return Metadado.flavor.equals(flavor);
	}

	@Override
	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
		if (Metadado.flavor.equals(flavor)) {
			return this;
		}

		throw new UnsupportedFlavorException(flavor);
	}

	public String getChaves() {
		for (Metadado titulo : filhos) {
			if (Constantes.PK.equals(titulo.descricao) || Constantes.PKS.equals(titulo.descricao)) {
				return titulo.getChaves2();
			}
		}

		return null;
	}

	public String getChaves2() {
		StringBuilder sb = new StringBuilder();

		if (!filhos.isEmpty()) {
			sb.append(filhos.get(0).descricao);
		}

		for (int i = 1; i < filhos.size(); i++) {
			sb.append("," + filhos.get(i).descricao);
		}

		return sb.toString();
	}

	public String pksMultiplas() {
		StringBuilder sb = new StringBuilder();

		for (Metadado table : filhos) {
			if (table.contem(Constantes.PKS)) {
				sb.append(table.descricao + Constantes.QL);
			}
		}

		return sb.toString();
	}

	public String pksAusente() {
		StringBuilder sb = new StringBuilder();

		for (Metadado table : filhos) {
			if (!table.contem(Constantes.PK) && !table.contem(Constantes.PKS)) {
				sb.append(table.descricao + Constantes.QL);
			}
		}

		return sb.toString();
	}

	public boolean contem(String descricao) {
		for (Metadado m : filhos) {
			if (m.descricao.equals(descricao)) {
				return true;
			}
		}

		return false;
	}
}