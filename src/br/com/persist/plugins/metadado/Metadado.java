package br.com.persist.plugins.metadado;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Mensagens;
import br.com.persist.assistencia.Util;

public class Metadado implements Transferable {
	public static final DataFlavor flavor = new DataFlavor(Metadado.class, "Metadado");
	private final List<String> ordenadoExportacao = new ArrayList<>();
	private final List<String> ordenadoImportacao = new ArrayList<>();
	private static final DataFlavor[] flavors = { flavor };
	private final List<Metadado> filhos;
	private final String descricao;
	private final String rotulo;
	private int totalImportados;
	private int totalExportados;
	private boolean ehRaiz;
	private boolean tabela;
	private Metadado pai;

	public Metadado(String descricao, String rotulo) {
		if (Util.estaVazio(descricao)) {
			throw new IllegalArgumentException();
		}
		this.descricao = descricao;
		filhos = new ArrayList<>();
		this.rotulo = rotulo;
	}

	public Metadado(String descricao) {
		this(descricao, descricao);
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

	public Metadado getMetadado(String descricao) {
		for (Metadado m : filhos) {
			if (m.descricao.equalsIgnoreCase(descricao)) {
				return m;
			}
		}
		return null;
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
		return rotulo;
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
			if (Constantes.CHAVE_PRIMARIA.equals(titulo.descricao)
					|| Constantes.CHAVES_PRIMARIAS.equals(titulo.descricao)) {
				return titulo.getChaves2();
			}
		}
		return null;
	}

	private String getChaves2() {
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
			if (table.contem(Constantes.CHAVES_PRIMARIAS)) {
				Metadado meta = table.getMetadado(Constantes.CHAVES_PRIMARIAS);
				sb.append(table.descricao + " - " + meta.getTotal() + Constantes.QL);
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
			if (!table.contem(Constantes.CHAVE_PRIMARIA) && !table.contem(Constantes.CHAVES_PRIMARIAS)) {
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
			if (table.contem(Constantes.CAMPO_EXPORTADO) || table.contem(Constantes.CAMPOS_EXPORTADOS)) {
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
			if (!table.contem(Constantes.CAMPO_EXPORTADO) && !table.contem(Constantes.CAMPOS_EXPORTADOS)) {
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
				if (tipo.descricao.equals(Constantes.CAMPO_EXPORTADO)
						|| tipo.descricao.equals(Constantes.CAMPOS_EXPORTADOS)) {
					tipo.listaStringExpImp(resposta);
				}
			}
		} else {
			for (Metadado tipo : filhos) {
				if (tipo.descricao.equals(Constantes.CAMPO_IMPORTADO)
						|| tipo.descricao.equals(Constantes.CAMPOS_IMPORTADOS)) {
					tipo.listaStringExpImp(resposta);
				}
			}
		}
		return resposta;
	}

	public String getFKPara(String tabelaIds) {
		StringBuilder sb = new StringBuilder();
		for (Metadado tipo : filhos) {
			if (tipo.descricao.equals(Constantes.CAMPO_IMPORTADO)
					|| tipo.descricao.equals(Constantes.CAMPOS_IMPORTADOS)) {
				tipo.fkPara(tabelaIds, sb);
			}
		}
		return sb.toString();
	}

	private void listaStringExpImp(List<String> resposta) {
		for (Metadado campo : filhos) {
			campo.listaString(resposta);
		}
	}

	private void fkPara(String tabelaIds, StringBuilder sb) {
		for (Metadado campo : filhos) {
			if (campo.fkPara(tabelaIds)) {
				sb.append(campo.descricao);
				break;
			}
		}
	}

	private void listaString(List<String> resposta) {
		for (Metadado tabelaIds : filhos) {
			resposta.add(tabelaIds.descricao);
		}
	}

	private boolean fkPara(String tabelaIds) {
		for (Metadado tab : filhos) {
			if (tab.descricao.equalsIgnoreCase(tabelaIds)) {
				return true;
			}
		}
		return false;
	}

	public boolean getEhRaiz() {
		return ehRaiz;
	}

	public void setEhRaiz(boolean ehRaiz) {
		this.ehRaiz = ehRaiz;
	}
}