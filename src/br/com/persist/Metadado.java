package br.com.persist;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import br.com.persist.util.Constantes;
import br.com.persist.util.Mensagens;
import br.com.persist.util.Util;

public class Metadado implements Transferable {
	public static final DataFlavor flavor = new DataFlavor(Metadado.class, "Metadado");
	private final List<String> ordenadoExportacao = new ArrayList<>();
	private final List<String> ordenadoImportacao = new ArrayList<>();
	private static final DataFlavor[] flavors = { flavor };
	private final List<Metadado> filhos;
	private final String descricao;
	private int totalImportados;
	private int totalExportados;
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
		int total = 0;

		for (Metadado table : filhos) {
			if (table.contem(Constantes.PKS)) {
				sb.append(table.descricao + Constantes.QL);
				total++;
			}
		}

		sb.insert(0, Mensagens.getString("label.pks_multiplas") + " [" + total + "]" + Constantes.QL + Constantes.QL);

		return sb.toString();
	}

	public String pksAusente() {
		StringBuilder sb = new StringBuilder();
		int total = 0;

		for (Metadado table : filhos) {
			if (!table.contem(Constantes.PK) && !table.contem(Constantes.PKS)) {
				sb.append(table.descricao + Constantes.QL);
				total++;
			}
		}

		sb.insert(0, Mensagens.getString("label.pks_ausente") + " [" + total + "]" + Constantes.QL + Constantes.QL);

		return sb.toString();
	}

	public String queExportam() {
		StringBuilder sb = new StringBuilder();
		int total = 0;

		for (Metadado table : filhos) {
			if (table.contem(Constantes.EK) || table.contem(Constantes.EKS)) {
				sb.append(table.descricao + Constantes.QL);
				total++;
			}
		}

		sb.insert(0,
				Mensagens.getString("label.tabelas_que_exportam") + " [" + total + "]" + Constantes.QL + Constantes.QL);

		return sb.toString();
	}

	public String naoExportam() {
		StringBuilder sb = new StringBuilder();
		int total = 0;

		for (Metadado table : filhos) {
			if (!table.contem(Constantes.EK) && !table.contem(Constantes.EKS)) {
				sb.append(table.descricao + Constantes.QL);
				total++;
			}
		}

		sb.insert(0,
				Mensagens.getString("label.tabelas_nao_exportam") + " [" + total + "]" + Constantes.QL + Constantes.QL);

		return sb.toString();
	}

	public String ordemExpImp(boolean exp) {
		StringBuilder sb = new StringBuilder(exp ? Mensagens.getString("label.ordenado_exportacao")
				: Mensagens.getString("label.ordenado_importacao"));
		sb.append(Constantes.QL);
		sb.append(Constantes.QL);

		if (exp) {
			for (String string : ordenadoExportacao) {
				sb.append(string + Constantes.QL);
			}
		} else {
			for (String string : ordenadoImportacao) {
				sb.append(string + Constantes.QL);
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

	public int getTotalImportados() {
		return totalImportados;
	}

	public void setTotalImportados(int totalImportados) {
		this.totalImportados = totalImportados;
	}

	public int getTotalExportados() {
		return totalExportados;
	}

	public void setTotalExportados(int totalExportados) {
		this.totalExportados = totalExportados;
	}

	public void montarOrdenacoes() {
		List<Metadado> temporario = new ArrayList<>(filhos);
		ordenadoExportacao.clear();
		ordenadoImportacao.clear();

		Collections.sort(temporario, (o1, o2) -> o2.getTotalExportados() - o1.getTotalExportados());

		for (Metadado meta : temporario) {
			ordenadoExportacao.add(meta.getTotalExportados() + " - " + meta.getDescricao());
		}

		Collections.sort(temporario, (o1, o2) -> o2.getTotalImportados() - o1.getTotalImportados());

		for (Metadado meta : temporario) {
			ordenadoImportacao.add(meta.getTotalImportados() + " - " + meta.getDescricao());
		}
	}

	public List<String> getListaStringExpImp(boolean exportacao) {
		List<String> resposta = new ArrayList<>();

		if (exportacao) {
			for (Metadado tipo : filhos) {
				if (tipo.descricao.equals(Constantes.EK) || tipo.descricao.equals(Constantes.EKS)) {
					tipo.listaStringExpImp(resposta);
				}
			}
		} else {
			for (Metadado tipo : filhos) {
				if (tipo.descricao.equals(Constantes.FK) || tipo.descricao.equals(Constantes.FKS)) {
					tipo.listaStringExpImp(resposta);
				}
			}
		}

		return resposta;
	}

	private void listaStringExpImp(List<String> resposta) {
		for (Metadado m : filhos) {
			m.listaString(resposta);
		}
	}

	private void listaString(List<String> resposta) {
		for (Metadado m : filhos) {
			resposta.add(m.descricao);
		}
	}
}