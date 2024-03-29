package br.com.persist.plugins.objeto.vinculo;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Util;
import br.com.persist.marca.XMLUtil;
import br.com.persist.plugins.objeto.Objeto;

public class ParaTabela {
	private final List<Instrucao> instrucoes;
	private final List<Filtro> filtros;
	private String esquemaAlternativo;
	private String tabelaAlternativo;
	private String prefixoNomeTabela;
	private String selectAlternativo;
	private String clonarAoDestacar;
	private String larguraRotulos;
	private String biblioChecagem;
	private String ajustarLargura;
	private String ajustarAltura;
	private String finalConsulta;
	private String transparente;
	private String complemento;
	private String classBiblio;
	private String destacaveis;
	private String colunaInfo;
	private String destacavel;
	private String sequencias;
	private String campoNomes;
	private String mapeamento;
	private String linkAuto;
	private String tabelas;
	private String apelido;
	private String orderBy;
	private Color corFonte;
	private Color corFundo;
	private String ignorar;
	private String chaves;
	private String joins;
	private String grupo;
	private String icone;
	private String sane;
	private String ccsc;
	private String bpnt;
	final String tabela;

	public ParaTabela(String tabela) {
		if (Util.isEmpty(tabela)) {
			throw new IllegalStateException("Tabela vazia.");
		}
		instrucoes = new ArrayList<>();
		filtros = new ArrayList<>();
		this.tabela = tabela;
	}

	public List<Instrucao> getInstrucoes() {
		return instrucoes;
	}

	public List<Filtro> getFiltros() {
		return filtros;
	}

	public void addInstrucao(String instrucao) {
		if (Util.isEmpty(instrucao)) {
			instrucao = "ALTERE PARA SUA INSTRUCAO";
		}
		Instrucao i = new Instrucao("Resumo");
		i.setSelecaoMultipla(true);
		i.setComoFiltro(false);
		i.setValor(instrucao);
		i.setOrdem(1);
		add(i);
	}

	public void add(Instrucao i) {
		if (i != null) {
			instrucoes.add(i);
		}
	}

	public void addFiltro(String filtro) {
		if (Util.isEmpty(filtro)) {
			filtro = "ALTERE PARA SEU FILTRO";
		}
		Filtro f = new Filtro("Resumo");
		f.setValor(filtro);
		f.setOrdem(1);
		add(f);
	}

	public void add(Filtro f) {
		if (f != null) {
			filtros.add(f);
		}
	}

	public void config(Objeto objeto) {
		if (!Util.isEmpty(clonarAoDestacar)) {
			objeto.setClonarAoDestacar(Boolean.parseBoolean(clonarAoDestacar));
		}
		if (!Util.isEmpty(ajustarLargura)) {
			objeto.setAjustarLargura(Boolean.parseBoolean(ajustarLargura));
		}
		if (!Util.isEmpty(ajustarAltura)) {
			objeto.setAjusteAutoForm(Boolean.parseBoolean(ajustarAltura));
		}
		if (!Util.isEmpty(transparente)) {
			objeto.setTransparente(Boolean.parseBoolean(transparente));
		}
		if (!Util.isEmpty(colunaInfo)) {
			objeto.setColunaInfo(Boolean.parseBoolean(colunaInfo));
		}
		if (!Util.isEmpty(destacavel)) {
			objeto.setAbrirAuto(Boolean.parseBoolean(destacavel));
		}
		if (!Util.isEmpty(linkAuto)) {
			objeto.setLinkAuto(Boolean.parseBoolean(linkAuto));
		}
		if (!Util.isEmpty(larguraRotulos)) {
			objeto.setLarguraRotulos(Boolean.parseBoolean(larguraRotulos));
		}
		if (!Util.isEmpty(prefixoNomeTabela)) {
			objeto.setPrefixoNomeTabela(prefixoNomeTabela);
		}
		if (!Util.isEmpty(selectAlternativo)) {
			objeto.setSelectAlternativo(selectAlternativo);
		}
		if (!Util.isEmpty(bpnt)) {
			objeto.setBpnt(Boolean.parseBoolean(bpnt));
		}
		if (!Util.isEmpty(finalConsulta)) {
			objeto.setFinalConsulta(finalConsulta);
		}
		if (!Util.isEmpty(apelido)) {
			objeto.setApelidoParaJoins(apelido);
		}
		if (!Util.isEmpty(biblioChecagem)) {
			objeto.setBiblioChecagem(biblioChecagem);
		}
		config2(objeto);
	}

	private void config2(Objeto objeto) {
		if (!Util.isEmpty(esquemaAlternativo)) {
			objeto.setEsquemaAlternativo(esquemaAlternativo);
		}
		if (!Util.isEmpty(ignorar)) {
			objeto.setIgnorar(Boolean.parseBoolean(ignorar));
		}
		if (!Util.isEmpty(tabelaAlternativo)) {
			objeto.setTabelaAlternativo(tabelaAlternativo);
		}
		if (!Util.isEmpty(sane)) {
			objeto.setSane(Boolean.parseBoolean(sane));
		}
		if (!Util.isEmpty(ccsc)) {
			objeto.setCcsc(Boolean.parseBoolean(ccsc));
		}
		if (!Util.isEmpty(complemento)) {
			objeto.setComplemento(complemento);
		}
		if (!Util.isEmpty(classBiblio)) {
			objeto.setClassBiblio(classBiblio);
		}
		if (!Util.isEmpty(destacaveis)) {
			objeto.setDestacaveis(destacaveis);
		}
		if (!Util.isEmpty(campoNomes)) {
			objeto.setChaveamento(campoNomes);
		}
		if (!Util.isEmpty(mapeamento)) {
			objeto.setMapeamento(mapeamento);
		}
		if (!Util.isEmpty(sequencias)) {
			objeto.setSequencias(sequencias);
		}
		if (corFonte != null) {
			objeto.setCorFonte(corFonte);
		}
		if (corFundo != null) {
			objeto.setCor(corFundo);
		}
		if (!Util.isEmpty(orderBy)) {
			objeto.setOrderBy(orderBy);
		}
		if (!Util.isEmpty(tabelas)) {
			objeto.setTabelas(tabelas);
		}
		config3(objeto);
	}

	private void config3(Objeto objeto) {
		if (!Util.isEmpty(chaves)) {
			objeto.setChaves(chaves);
		}
		if (!Util.isEmpty(joins)) {
			objeto.setJoins(joins);
		}
		if (!Util.isEmpty(grupo)) {
			objeto.setGrupo(grupo);
		}
		if (icone != null) {
			objeto.setIcone(icone);
		}
	}

	public void salvar(XMLUtil util, boolean ql) {
		if (ql) {
			util.ql();
		}
		util.abrirTag(VinculoHandler.PARA);
		atributoValor(util, VinculoHandler.TABELA, tabela);
		atributoValor(util, VinculoHandler.ICONE, icone);
		if (corFonte != null) {
			atributoValor(util, VinculoHandler.COR_FONTE, Referencia.toHex(corFonte));
		}
		if (corFundo != null) {
			atributoValor(util, VinculoHandler.COR_FUNDO, Referencia.toHex(corFundo));
		}
		atributoValor(util, "esquemaAlternativo", esquemaAlternativo);
		atributoValor(util, "tabelaAlternativo", tabelaAlternativo);
		atributoValor(util, "prefixoNomeTabela", prefixoNomeTabela);
		atributoValor(util, "selectAlternativo", selectAlternativo);
		atributoValor(util, "clonarAoDestacar", clonarAoDestacar);
		atributoValor(util, "larguraRotulos", larguraRotulos);
		atributoValor(util, "biblioChecagem", biblioChecagem);
		atributoValor(util, "ajustarLargura", ajustarLargura);
		atributoValor(util, "finalConsulta", finalConsulta);
		atributoValor(util, "ajustarAltura", ajustarAltura);
		atributoValor(util, "transparente", transparente);
		atributoValor(util, "complemento", complemento);
		atributoValor(util, "classBiblio", classBiblio);
		atributoValor(util, "destacaveis", destacaveis);
		atributoValor(util, "mapeamento", mapeamento);
		atributoValor(util, "sequencias", sequencias);
		atributoValor(util, "campoNomes", campoNomes);
		atributoValor(util, "colunaInfo", colunaInfo);
		atributoValor(util, "destacavel", destacavel);
		atributoValor(util, "linkAuto", linkAuto);
		atributoValor(util, "apelido", apelido);
		atributoValor(util, "orderBy", orderBy);
		atributoValor(util, "tabelas", tabelas);
		atributoValor(util, "ignorar", ignorar);
		atributoValor(util, "chaves", chaves);
		atributoValor(util, "joins", joins);
		atributoValor(util, VinculoHandler.GRUPO, grupo);
		atributoValor(util, "sane", sane);
		atributoValor(util, "ccsc", ccsc);
		atributoValor(util, "bpnt", bpnt);
		util.fecharTag();
		ql = false;
		for (Instrucao i : instrucoes) {
			i.salvar(util, ql);
			ql = true;
		}
		for (Filtro f : filtros) {
			f.salvar(util, ql);
			ql = true;
		}
		util.finalizarTag(VinculoHandler.PARA);
	}

	public static void atributoValor(XMLUtil util, String nome, boolean valor) {
		if (valor) {
			util.atributo(nome, valor);
		}
	}

	public static void atributoValor(XMLUtil util, String nome, String valor) {
		Referencia.atributoValor(util, nome, valor);
	}

	public void modelo(XMLUtil util) {
		util.abrirTag(VinculoHandler.PARA).atributo(VinculoHandler.TABELA, VinculoHandler.NOME_TABELA)
				.atributo(VinculoHandler.ICONE, "nome_icone").atributo(VinculoHandler.COR_FONTE, "#AABBCC")
				.atributo(VinculoHandler.COR_FUNDO, "#CAFEBB").ql();
		util.tab().atributo("prefixoNomeTabela", "H_").ql();
		util.tab().atributo("clonarAoDestacar", true).ql();
		util.tab().atributo(VinculoHandler.GRUPO, "").ql();
		util.tab().atributo("esquemaAlternativo", "").ql();
		util.tab().atributo("selectAlternativo", "").ql();
		util.tab().atributo("tabelaAlternativo", "").ql();
		util.tab().atributo("larguraRotulos", false).ql();
		util.tab().atributo("checarRegistro", false).ql();
		util.tab().atributo("ajustarLargura", true).ql();
		util.tab().atributo("ajustarAltura", true).ql();
		util.tab().atributo("transparente", false).ql();
		util.tab().atributo("colunaInfo", false).ql();
		util.tab().atributo("finalConsulta", "").ql();
		util.tab().atributo("destacavel", true).ql();
		util.tab().atributo("complemento", "").ql();
		util.tab().atributo("classBiblio", "").ql();
		util.tab().atributo("destacaveis", "").ql();
		util.tab().atributo("mapeamento", "").ql();
		util.tab().atributo("sequencias", "").ql();
		util.tab().atributo("campoNomes", "").ql();
		util.tab().atributo("linkAuto", true).ql();
		util.tab().atributo("apelido", "ape").ql();
		util.tab().atributo("ignorar", false).ql();
		util.tab().atributo("orderBy", "").ql();
		util.tab().atributo("tabelas", "").ql();
		util.tab().atributo("chaves", "").ql();
		util.tab().atributo("joins", "").ql();
		util.tab().atributo("sane", true).ql();
		util.tab().atributo("ccsc", true).ql();
		util.tab().atributo("bpnt", false).fecharTag();

		Instrucao i = new Instrucao("Resumo da instrucao");
		i.setValor("UPDATE candidato SET votos = 0 WHERE id = " + Constantes.SEP + "id" + Constantes.SEP);
		i.salvar(util, false);

		Filtro f = new Filtro("Resumo do filtro");
		f.setValor("AND id = 1 AND descricao LIKE '%Descricao%'");
		f.salvar(util, false);

		util.finalizarTag(VinculoHandler.PARA);
	}

	public String getTabela() {
		return tabela;
	}

	public String getIcone() {
		return icone;
	}

	@Override
	public String toString() {
		return tabela;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof ParaTabela) {
			ParaTabela outro = (ParaTabela) obj;
			return tabela.equals(outro.tabela);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return tabela.hashCode();
	}

	public String getApelido() {
		return apelido;
	}

	public void setApelido(String apelido) {
		this.apelido = apelido;
	}

	public String getSequencias() {
		return sequencias;
	}

	public void setSequencias(String sequencias) {
		this.sequencias = sequencias;
	}

	public String getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

	public String getPrefixoNomeTabela() {
		return prefixoNomeTabela;
	}

	public void setPrefixoNomeTabela(String prefixoNomeTabela) {
		this.prefixoNomeTabela = prefixoNomeTabela;
	}

	public String getSelectAlternativo() {
		return selectAlternativo;
	}

	public void setSelectAlternativo(String selectAlternativo) {
		this.selectAlternativo = selectAlternativo;
	}

	public String getFinalConsulta() {
		return finalConsulta;
	}

	public void setFinalConsulta(String finalConsulta) {
		this.finalConsulta = finalConsulta;
	}

	public String getComplemento() {
		return complemento;
	}

	public void setComplemento(String complemento) {
		this.complemento = complemento;
	}

	public String getClassBiblio() {
		return classBiblio;
	}

	public void setClassBiblio(String classBiblio) {
		this.classBiblio = classBiblio;
	}

	public String getDestacaveis() {
		return destacaveis;
	}

	public void setDestacaveis(String destacaveis) {
		this.destacaveis = destacaveis;
	}

	public String getCampoNomes() {
		return campoNomes;
	}

	public void setCampoNomes(String campoNomes) {
		this.campoNomes = campoNomes;
	}

	public String getMapeamento() {
		return mapeamento;
	}

	public void setMapeamento(String mapeamento) {
		this.mapeamento = mapeamento;
	}

	public String getChaves() {
		return chaves;
	}

	public void setChaves(String chaves) {
		this.chaves = chaves;
	}

	public String getGrupo() {
		return grupo;
	}

	public void setGrupo(String grupo) {
		this.grupo = grupo;
	}

	public Color getCorFonte() {
		return corFonte;
	}

	public Color getCorFundo() {
		return corFundo;
	}

	public String getAjustarAltura() {
		return ajustarAltura;
	}

	public void setAjustarAltura(String ajustarAltura) {
		this.ajustarAltura = ajustarAltura;
	}

	public String getAjustarLargura() {
		return ajustarLargura;
	}

	public void setAjustarLargura(String ajustarLargura) {
		this.ajustarLargura = ajustarLargura;
	}

	public String getColunaInfo() {
		return colunaInfo;
	}

	public void setColunaInfo(String colunaInfo) {
		this.colunaInfo = colunaInfo;
	}

	public String getDestacavel() {
		return destacavel;
	}

	public void setDestacavel(String destacavel) {
		this.destacavel = destacavel;
	}

	public String getLinkAuto() {
		return linkAuto;
	}

	public void setLinkAuto(String linkAuto) {
		this.linkAuto = linkAuto;
	}

	public String getLarguraRotulos() {
		return larguraRotulos;
	}

	public void setLarguraRotulos(String larguraRotulos) {
		this.larguraRotulos = larguraRotulos;
	}

	public String getSane() {
		return sane;
	}

	public void setSane(String sane) {
		this.sane = sane;
	}

	public String getIgnorar() {
		return ignorar;
	}

	public void setIgnorar(String ignorar) {
		this.ignorar = ignorar;
	}

	public String getCcsc() {
		return ccsc;
	}

	public void setCcsc(String ccsc) {
		this.ccsc = ccsc;
	}

	public String getBpnt() {
		return bpnt;
	}

	public void setBpnt(String bpnt) {
		this.bpnt = bpnt;
	}

	public void setCorFonte(Color corFonte) {
		this.corFonte = corFonte;
	}

	public void setCorFundo(Color corFundo) {
		this.corFundo = corFundo;
	}

	public void setIcone(String icone) {
		this.icone = icone;
	}

	public String getTabelas() {
		return tabelas;
	}

	public void setTabelas(String tabelas) {
		this.tabelas = tabelas;
	}

	public String getEsquemaAlternativo() {
		return esquemaAlternativo;
	}

	public void setEsquemaAlternativo(String esquemaAlternativo) {
		this.esquemaAlternativo = esquemaAlternativo;
	}

	public String getTabelaAlternativo() {
		return tabelaAlternativo;
	}

	public void setTabelaAlternativo(String tabelaAlternativo) {
		this.tabelaAlternativo = tabelaAlternativo;
	}

	public String getJoins() {
		return joins;
	}

	public void setJoins(String joins) {
		this.joins = joins;
	}

	public String getTransparente() {
		return transparente;
	}

	public String getBiblioChecagem() {
		return biblioChecagem;
	}

	public void setTransparente(String transparente) {
		this.transparente = transparente;
	}

	public void setBiblioChecagem(String biblioChecagem) {
		this.biblioChecagem = biblioChecagem;
	}

	public String getClonarAoDestacar() {
		return clonarAoDestacar;
	}

	public void setClonarAoDestacar(String clonarAoDestacar) {
		this.clonarAoDestacar = clonarAoDestacar;
	}
}