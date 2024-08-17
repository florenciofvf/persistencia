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

import br.com.persist.assistencia.ArgumentoException;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Util;
import br.com.persist.marca.XMLUtil;

public class Metadado implements Transferable {
	public static final DataFlavor flavor = new DataFlavor(Metadado.class, "Metadado");
	private final List<String> ordenadoExportacao = new ArrayList<>();
	private final List<String> ordenadoImportacao = new ArrayList<>();
	private final List<String> ordenadoCampos = new ArrayList<>();
	private static final DataFlavor[] flavors = { flavor };
	private boolean contabilizarCampos = true;
	private final boolean contabilizavel;
	private final List<Metadado> filhos;
	private boolean contabilizar = true;
	private final String descricao;
	private int totalImportados;
	private int totalExportados;
	private boolean selecionado;
	private boolean constraint;
	private int totalCampos;
	private boolean ehRaiz;
	private boolean tabela;
	private Metadado pai;
	private String tag;

	public Metadado(String descricao, boolean contabilizavel) throws ArgumentoException {
		if (Util.isEmpty(descricao)) {
			throw new ArgumentoException("descricao vazia.");
		}
		this.contabilizavel = contabilizavel;
		this.descricao = descricao;
		filhos = new ArrayList<>();
	}

	public void salvar(XMLUtil util) {
		util.abrirTag(MetadadoConstantes.METADADO);
		util.atributo("descricao", descricao);
		util.atributo("tabela", tabela);
		util.atributo("constraint", constraint);
		util.atributo("tag", getTag());
		util.atributo("contabilizavel", contabilizavel);
		util.fecharTag();
		for (Metadado m : filhos) {
			m.salvar(util);
		}
		util.finalizarTag(MetadadoConstantes.METADADO);
	}

	public void aplicar(Attributes attr) {
		tag = attr.getValue("tag");
		tabela = Boolean.parseBoolean(attr.getValue("tabela"));
		constraint = Boolean.parseBoolean(attr.getValue("constraint"));
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

	public Metadado getMetadado(String descricao, boolean porParte) {
		for (Metadado m : filhos) {
			if (porParte && m.descricao.toUpperCase().indexOf(descricao.toUpperCase()) != -1) {
				return m;
			}
			if (m.descricao.equalsIgnoreCase(descricao)) {
				return m;
			}
		}
		for (Metadado m : filhos) {
			Metadado resp = m.getMetadado(descricao, porParte);
			if (resp != null) {
				return resp;
			}
		}
		return null;
	}

	public void preencher(List<Metadado> lista, String descricao, boolean porParte) {
		if ((porParte && this.descricao.toUpperCase().indexOf(descricao.toUpperCase()) != -1)
				|| this.descricao.equalsIgnoreCase(descricao)) {
			lista.add(this);
		}
		for (Metadado m : filhos) {
			m.preencher(lista, descricao, porParte);
		}
	}

	public Metadado getMetadado(String descricao) {
		return getMetadado(descricao, false);
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
			if (MetadadoConstantes.CHAVE_PRIMARIA.equals(titulo.descricao)
					|| MetadadoConstantes.CHAVES_PRIMARIAS.equals(titulo.descricao)) {
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
			Metadado meta = table.getMetadado(MetadadoConstantes.CHAVES_PRIMARIAS);
			if (meta != null) {
				sb.append(meta.getTotal() + " - " + table.descricao + Constantes.QL);
				total++;
			}
		}
		return sb.insert(0, MetadadoMensagens.getString("label.pks_multiplas") + " [" + total + "]" + Constantes.QL2)
				.toString();
	}

	public String pksMultiplasExport() {
		StringBuilder sb = new StringBuilder();
		int total = 0;
		for (Metadado table : filhos) {
			Metadado meta = table.getMetadado(MetadadoConstantes.CHAVES_PRIMARIAS);
			if (meta != null) {
				table.contabilizarExportacao();
				if (table.totalExportados > 0) {
					sb.append(meta.getTotal() + " - " + table.descricao + " exportados=" + table.totalExportados
							+ Constantes.QL);
					total++;
				}
			}
		}
		return sb
				.insert(0,
						MetadadoMensagens.getString("label.pks_multiplas_export") + " [" + total + "]" + Constantes.QL2)
				.toString();
	}

	public String pksAusente() {
		StringBuilder sb = new StringBuilder();
		int total = 0;
		for (Metadado table : filhos) {
			if (!table.contem(MetadadoConstantes.CHAVE_PRIMARIA)
					&& !table.contem(MetadadoConstantes.CHAVES_PRIMARIAS)) {
				sb.append(table.descricao + Constantes.QL);
				total++;
			}
		}
		return sb.insert(0, MetadadoMensagens.getString("label.pks_ausente") + " [" + total + "]" + Constantes.QL2)
				.toString();
	}

	public String queExportam() {
		StringBuilder sb = new StringBuilder();
		int total = queExportam(sb);
		return sb
				.insert(0,
						MetadadoMensagens.getString("label.tabelas_que_exportam") + " [" + total + "]" + Constantes.QL2)
				.toString();
	}

	private int queExportam(StringBuilder sb) {
		int total = 0;
		for (Metadado table : filhos) {
			table.contabilizarExportacao();
			if (table.totalExportados > 0) {
				sb.append(table.descricao + " - " + table.totalExportados + Constantes.QL);
				total++;
			}
		}
		return total;
	}

	public String naoExportam() {
		StringBuilder sb = new StringBuilder();
		int total = naoExportam(sb);
		return sb
				.insert(0,
						MetadadoMensagens.getString("label.tabelas_nao_exportam") + " [" + total + "]" + Constantes.QL2)
				.toString();
	}

	private int naoExportam(StringBuilder sb) {
		int total = 0;
		for (Metadado table : filhos) {
			table.contabilizarExportacao();
			if (table.totalExportados == 0) {
				sb.append(table.descricao + Constantes.QL);
				total++;
			}
		}
		return total;
	}

	public String getOrdenadosCampos() {
		StringBuilder sb = new StringBuilder(MetadadoMensagens.getString("label.ordenado_campos"));
		sb.append(Constantes.QL2);
		checarContabilizacaoCampos();
		for (String string : ordenadoCampos) {
			sb.append(string + Constantes.QL);
		}
		return sb.toString();
	}

	public String getOrdenadosExportacaoImportacao(boolean exportacao) {
		StringBuilder sb = new StringBuilder(exportacao ? MetadadoMensagens.getString("label.ordenado_exportacao")
				: MetadadoMensagens.getString("label.ordenado_importacao"));
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

	private void checarContabilizacaoCampos() {
		if (contabilizarCampos) {
			for (Metadado tab : filhos) {
				tab.contabilizarCampos();
			}
			contabilizarCampos = false;
			montarOrdenacoesCampos();
		}
	}

	private void contabilizarImportacaoExportacao() {
		contabilizarImportacao();
		contabilizarExportacao();
	}

	private void contabilizarCampos() {
		totalCampos = 0;
		Metadado metadado = getMetadado(Constantes.CAMPO);
		if (metadado != null) {
			totalCampos += metadado.getTotal();
		}
		metadado = getMetadado(Constantes.CAMPOS);
		if (metadado != null) {
			totalCampos += metadado.getTotal();
		}
	}

	private void contabilizarImportacao() {
		totalImportados = 0;
		Metadado metadado = getMetadado(MetadadoConstantes.CAMPO_IMPORTADO);
		if (metadado != null) {
			totalImportados += metadado.getTotal();
		}
		metadado = getMetadado(MetadadoConstantes.CAMPOS_IMPORTADOS);
		if (metadado != null) {
			totalImportados += metadado.getTotal();
		}
	}

	private void contabilizarExportacao() {
		totalExportados = 0;
		Metadado metadado = getMetadado(MetadadoConstantes.CAMPO_EXPORTADO);
		if (metadado != null) {
			totalExportados += metadado.getTotal();
		}
		metadado = getMetadado(MetadadoConstantes.CAMPOS_EXPORTADOS);
		if (metadado != null) {
			totalExportados += metadado.getTotal();
		}
	}

	public List<String> getListaCampoExportadoPara(String campo) throws MetadadoException {
		List<String> lista = new ArrayList<>();
		List<Metadado> campos = getListaCampoExportacaoImportacao(true);
		for (Metadado metadado : campos) {
			if (metadado.getDescricao().equalsIgnoreCase(campo)) {
				lista.add(metadado.getTabelaReferencia().getDescricao());
			}
		}
		return lista;
	}

	public List<String> getListaCampoImportadoDe(String campo) throws MetadadoException {
		List<String> lista = new ArrayList<>();
		List<Metadado> campos = getListaCampoExportacaoImportacao(false);
		for (Metadado metadado : campos) {
			if (metadado.getDescricao().equalsIgnoreCase(campo)) {
				lista.add(metadado.getTabelaReferencia().getDescricao());
			}
		}
		return lista;
	}

	private void montarOrdenacoes() {
		List<Metadado> temporario = new ArrayList<>(filhos);
		montarOrdenacoesExportacoes(temporario);
		montarOrdenacoesImportacoes(temporario);
	}

	private void montarOrdenacoesCampos() {
		List<Metadado> temporario = new ArrayList<>(filhos);
		ordenadoCampos.clear();
		Collections.sort(temporario, (o1, o2) -> o2.totalCampos - o1.totalCampos);
		for (Metadado meta : temporario) {
			ordenadoCampos.add(meta.totalCampos + " - " + meta.descricao);
		}
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

	public List<Metadado> getListaCampoExportacaoImportacao(boolean exportacao) {
		if (exportacao) {
			return getListaCampoExportacao();
		}
		return getListaCampoImportacao();
	}

	private List<Metadado> getListaCampoImportacao() {
		List<Metadado> lista = new ArrayList<>();
		for (Metadado tipo : filhos) {
			if (tipo.descricao.equals(MetadadoConstantes.CAMPO_IMPORTADO)
					|| tipo.descricao.equals(MetadadoConstantes.CAMPOS_IMPORTADOS)) {
				tipo.preencher(lista);
			}
		}
		return lista;
	}

	private List<Metadado> getListaCampoExportacao() {
		List<Metadado> lista = new ArrayList<>();
		for (Metadado tipo : filhos) {
			if (tipo.descricao.equals(MetadadoConstantes.CAMPO_EXPORTADO)
					|| tipo.descricao.equals(MetadadoConstantes.CAMPOS_EXPORTADOS)) {
				tipo.preencher(lista);
			}
		}
		return lista;
	}

	public void preencher(List<Metadado> lista) {
		for (Metadado campo : filhos) {
			lista.add(campo);
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

	public boolean getEhRaiz() {
		return ehRaiz;
	}

	public void setEhRaiz(boolean ehRaiz) {
		this.ehRaiz = ehRaiz;
	}

	public String getNomeTabela() {
		int pos = descricao.indexOf('(');
		return descricao.substring(0, pos);
	}

	public String getNomeCampo() {
		int pos = descricao.indexOf('(');
		int pos2 = descricao.indexOf(')');
		return descricao.substring(pos + 1, pos2);
	}

	public Metadado getTabelaReferencia() throws MetadadoException {
		if (filhos.size() != 1) {
			throw new MetadadoException("getTabelaReferencia(): filhos.size() != 1", false);
		}
		return filhos.get(0);
	}

	public String getChaveTabelaReferencia() throws MetadadoException {
		return descricao + " > " + getTabelaReferencia().descricao;
	}

	public boolean isConstraint() {
		return constraint;
	}

	public void setConstraint(boolean constraint) {
		this.constraint = constraint;
	}

	public String getTag() {
		if (Util.isEmpty(tag)) {
			tag = Constantes.VAZIO;
		}
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public boolean isSelecionado() {
		return selecionado;
	}

	public void setSelecionado(boolean selecionado) {
		this.selecionado = selecionado;
	}

	public void copiarDescricao() {
		Util.setContentTransfered(toString());
	}
}