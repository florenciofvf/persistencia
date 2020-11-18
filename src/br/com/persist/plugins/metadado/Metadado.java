package br.com.persist.plugins.metadado;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.xml.sax.Attributes;

import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Mensagens;
import br.com.persist.assistencia.Util;
import br.com.persist.marca.XMLUtil;

public class Metadado implements Transferable {
	public static final DataFlavor flavor = new DataFlavor(Metadado.class, "Metadado");
	private final List<String> ordenadoExportacao = new ArrayList<>();
	private final List<String> ordenadoImportacao = new ArrayList<>();
	private static final DataFlavor[] flavors = { flavor };
	private final boolean contabilizavel;
	private final List<Metadado> filhos;
	private boolean contabilizar = true;
	private final String descricao;
	private int totalImportados;
	private int totalExportados;
	private boolean ehRaiz;
	private boolean tabela;
	private Metadado pai;

	public Metadado(String descricao, boolean contabilizavel) {
		if (Util.estaVazio(descricao)) {
			throw new IllegalArgumentException();
		}
		this.contabilizavel = contabilizavel;
		this.descricao = descricao;
		filhos = new ArrayList<>();
	}

	public void salvar(XMLUtil util) {
		util.abrirTag(Constantes.METADADO);
		util.atributo("descricao", descricao);
		util.atributo("tabela", tabela);
		util.atributo("contabilizavel", contabilizavel);
		util.fecharTag();
		for (Metadado m : filhos) {
			m.salvar(util);
		}
		util.finalizarTag(Constantes.METADADO);
	}

	public void aplicar(Attributes attr) {
		tabela = Boolean.parseBoolean(attr.getValue("tabela"));
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

	public boolean contem(String descricao) {
		return getMetadado(descricao) != null;
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
		if (contabilizavel) {
			int total = getTotal();
			return descricao + (total > 1 ? " - " + total : "");
		}
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
		sb.insert(0, Mensagens.getString("label.pks_multiplas") + " [" + total + "]" + Constantes.QL2);
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
		sb.insert(0, Mensagens.getString("label.pks_ausente") + " [" + total + "]" + Constantes.QL2);
		return sb.toString();
	}

	public String queExportam() {
		StringBuilder sb = new StringBuilder();
		int total = queExportam(sb);
		sb.insert(0, Mensagens.getString("label.tabelas_que_exportam") + " [" + total + "]" + Constantes.QL2);
		return sb.toString();
	}

	private int queExportam(StringBuilder sb) {
		int total = 0;
		for (Metadado table : filhos) {
			if (table.contem(Constantes.CAMPO_EXPORTADO) || table.contem(Constantes.CAMPOS_EXPORTADOS)) {
				sb.append(table.descricao + Constantes.QL);
				total++;
			}
		}
		return total;
	}

	public String naoExportam() {
		StringBuilder sb = new StringBuilder();
		int total = naoExportam(sb);
		sb.insert(0, Mensagens.getString("label.tabelas_nao_exportam") + " [" + total + "]" + Constantes.QL2);
		return sb.toString();
	}

	private int naoExportam(StringBuilder sb) {
		int total = 0;
		for (Metadado table : filhos) {
			if (!table.contem(Constantes.CAMPO_EXPORTADO) && !table.contem(Constantes.CAMPOS_EXPORTADOS)) {
				sb.append(table.descricao + Constantes.QL);
				total++;
			}
		}
		return total;
	}

	public String getOrdenadosExportacaoImportacao(boolean exportacao) {
		StringBuilder sb = new StringBuilder(exportacao ? Mensagens.getString("label.ordenado_exportacao")
				: Mensagens.getString("label.ordenado_importacao"));
		sb.append(Constantes.QL2);
		checarContabilizacao();
		ordenarExportacaoImportacao(exportacao, sb);
		return sb.toString();
	}

	private void ordenarExportacaoImportacao(boolean exportacao, StringBuilder sb) {
		if (exportacao) {
			for (String string : ordenadoExportacao) {
				sb.append(string + Constantes.QL);
			}
		} else {
			for (String string : ordenadoImportacao) {
				sb.append(string + Constantes.QL);
			}
		}
	}

	private void checarContabilizacao() {
		if (contabilizar) {
			for (Metadado tab : filhos) {
				tab.contabilizarImportacaoExportacao();
			}
			contabilizar = false;
			montarOrdenacoes();
		}
	}

	private void contabilizarImportacaoExportacao() {
		contabilizarImportacao();
		contabilizarExportacao();
	}

	private void contabilizarImportacao() {
		totalImportados = 0;
		Metadado metadado = getMetadado(Constantes.CAMPO_IMPORTADO);
		if (metadado != null) {
			totalImportados += metadado.getTotal();
		}
		metadado = getMetadado(Constantes.CAMPOS_IMPORTADOS);
		if (metadado != null) {
			totalImportados += metadado.getTotal();
		}
	}

	private void contabilizarExportacao() {
		totalExportados = 0;
		Metadado metadado = getMetadado(Constantes.CAMPO_EXPORTADO);
		if (metadado != null) {
			totalExportados += metadado.getTotal();
		}
		metadado = getMetadado(Constantes.CAMPOS_EXPORTADOS);
		if (metadado != null) {
			totalExportados += metadado.getTotal();
		}
	}

	private void montarOrdenacoes() {
		List<Metadado> temporario = new ArrayList<>(filhos);
		montarOrdenacoesExportacoes(temporario);
		montarOrdenacoesImportacoes(temporario);
	}

	private void montarOrdenacoesImportacoes(List<Metadado> temporario) {
		ordenadoImportacao.clear();
		Collections.sort(temporario, (o1, o2) -> o2.totalImportados - o1.totalImportados);
		for (Metadado meta : temporario) {
			ordenadoImportacao.add(meta.totalImportados + " - " + meta.descricao);
		}
	}

	private void montarOrdenacoesExportacoes(List<Metadado> temporario) {
		ordenadoExportacao.clear();
		Collections.sort(temporario, (o1, o2) -> o2.totalExportados - o1.totalExportados);
		for (Metadado meta : temporario) {
			ordenadoExportacao.add(meta.totalExportados + " - " + meta.descricao);
		}
	}

	public List<String> getListaDescricaoExportacaoImportacao(boolean exportacao) {
		List<String> resposta = new ArrayList<>();
		if (exportacao) {
			getListaDescricaoExportacao(resposta);
		} else {
			getListaDescricaoImportacao(resposta);
		}
		return resposta;
	}

	private void getListaDescricaoImportacao(List<String> resposta) {
		for (Metadado tipo : filhos) {
			if (tipo.descricao.equals(Constantes.CAMPO_IMPORTADO)
					|| tipo.descricao.equals(Constantes.CAMPOS_IMPORTADOS)) {
				tipo.listarDescricaoExportacaoImportacao(resposta);
			}
		}
	}

	private void getListaDescricaoExportacao(List<String> resposta) {
		for (Metadado tipo : filhos) {
			if (tipo.descricao.equals(Constantes.CAMPO_EXPORTADO)
					|| tipo.descricao.equals(Constantes.CAMPOS_EXPORTADOS)) {
				tipo.listarDescricaoExportacaoImportacao(resposta);
			}
		}
	}

	public Map<String, Set<String>> localizarCampo(String nome) {
		Map<String, Set<String>> resp = new LinkedHashMap<>();
		for (Metadado tab : filhos) {
			tab.montarNaTabela(nome, tab.descricao, resp);
		}
		return resp;
	}

	private void montarNaTabela(String nome, String tabela, Map<String, Set<String>> map) {
		for (Metadado tipo : filhos) {
			tipo.montarNoTipo(nome, tabela, map);
		}
	}

	private void montarNoTipo(String nome, String tabela, Map<String, Set<String>> map) {
		for (Metadado campo : filhos) {
			if (campo.descricao.toUpperCase().indexOf(nome) != -1) {
				Set<String> lista = map.computeIfAbsent(tabela, t -> new LinkedHashSet<>());
				lista.add(campo.descricao);
			}
		}
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

	private void listarDescricaoExportacaoImportacao(List<String> resposta) {
		for (Metadado campo : filhos) {
			campo.listarDescricao(resposta);
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

	private void listarDescricao(List<String> resposta) {
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