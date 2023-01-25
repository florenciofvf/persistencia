package br.com.persist.plugins.objeto.vinculo;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

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
	private String checarRegistro;
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
	private String chaves;
	private String joins;
	private String grupo;
	private String icone;
	private String sane;
	private String ccsc;
	private String bpnt;
	final String tabela;

	public ParaTabela(String tabela) {
		if (Util.estaVazio(tabela)) {
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
		if (Util.estaVazio(instrucao)) {
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
		if (Util.estaVazio(filtro)) {
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
		if (!Util.estaVazio(clonarAoDestacar)) {
			objeto.setClonarAoDestacar(Boolean.parseBoolean(clonarAoDestacar));
		}
		if (!Util.estaVazio(checarRegistro)) {
			objeto.setChecarRegistro(Boolean.parseBoolean(checarRegistro));
		}
		if (!Util.estaVazio(ajustarAltura)) {
			objeto.setAjusteAutoForm(Boolean.parseBoolean(ajustarAltura));
		}
		if (!Util.estaVazio(transparente)) {
			objeto.setTransparente(Boolean.parseBoolean(transparente));
		}
		if (!Util.estaVazio(colunaInfo)) {
			objeto.setColunaInfo(Boolean.parseBoolean(colunaInfo));
		}
		if (!Util.estaVazio(destacavel)) {
			objeto.setAbrirAuto(Boolean.parseBoolean(destacavel));
		}
		if (!Util.estaVazio(linkAuto)) {
			objeto.setLinkAuto(Boolean.parseBoolean(linkAuto));
		}
		if (!Util.estaVazio(larguraRotulos)) {
			objeto.setLarguraRotulos(Boolean.parseBoolean(larguraRotulos));
		}
		if (!Util.estaVazio(prefixoNomeTabela)) {
			objeto.setPrefixoNomeTabela(prefixoNomeTabela);
		}
		if (!Util.estaVazio(selectAlternativo)) {
			objeto.setSelectAlternativo(selectAlternativo);
		}
		if (!Util.estaVazio(bpnt)) {
			objeto.setBpnt(Boolean.parseBoolean(bpnt));
		}
		if (!Util.estaVazio(finalConsulta)) {
			objeto.setFinalConsulta(finalConsulta);
		}
		if (!Util.estaVazio(apelido)) {
			objeto.setApelidoParaJoins(apelido);
		}
		config2(objeto);
	}

	private void config2(Objeto objeto) {
		if (!Util.estaVazio(esquemaAlternativo)) {
			objeto.setEsquemaAlternativo(esquemaAlternativo);
		}
		if (!Util.estaVazio(tabelaAlternativo)) {
			objeto.setTabelaAlternativo(tabelaAlternativo);
		}
		if (!Util.estaVazio(sane)) {
			objeto.setSane(Boolean.parseBoolean(sane));
		}
		if (!Util.estaVazio(ccsc)) {
			objeto.setCcsc(Boolean.parseBoolean(ccsc));
		}
		if (!Util.estaVazio(complemento)) {
			objeto.setComplemento(complemento);
		}
		if (!Util.estaVazio(classBiblio)) {
			objeto.setClassBiblio(classBiblio);
		}
		if (!Util.estaVazio(destacaveis)) {
			objeto.setDestacaveis(destacaveis);
		}
		if (!Util.estaVazio(campoNomes)) {
			objeto.setChaveamento(campoNomes);
		}
		if (!Util.estaVazio(mapeamento)) {
			objeto.setMapeamento(mapeamento);
		}
		if (!Util.estaVazio(sequencias)) {
			objeto.setSequencias(sequencias);
		}
		if (corFonte != null) {
			objeto.setCorFonte(corFonte);
		}
		if (!Util.estaVazio(orderBy)) {
			objeto.setOrderBy(orderBy);
		}
		if (!Util.estaVazio(tabelas)) {
			objeto.setTabelas(tabelas);
		}
		config3(objeto);
	}

	private void config3(Objeto objeto) {
		if (!Util.estaVazio(chaves)) {
			objeto.setChaves(chaves);
		}
		if (!Util.estaVazio(joins)) {
			objeto.setJoins(joins);
		}
		if (!Util.estaVazio(grupo)) {
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
		atributoValor(util, "esquemaAlternativo", esquemaAlternativo);
		atributoValor(util, "tabelaAlternativo", tabelaAlternativo);
		atributoValor(util, "prefixoNomeTabela", prefixoNomeTabela);
		atributoValor(util, "selectAlternativo", selectAlternativo);
		atributoValor(util, "clonarAoDestacar", clonarAoDestacar);
		atributoValor(util, "larguraRotulos", larguraRotulos);
		atributoValor(util, "checarRegistro", checarRegistro);
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
				.atributo(VinculoHandler.ICONE, "nome_icone").atributo(VinculoHandler.COR_FONTE, "#AABBCC").ql();
		util.tab().atributo("prefixoNomeTabela", "H_").ql();
		util.tab().atributo("clonarAoDestacar", true).ql();
		util.tab().atributo(VinculoHandler.GRUPO, "").ql();
		util.tab().atributo("esquemaAlternativo", "").ql();
		util.tab().atributo("selectAlternativo", "").ql();
		util.tab().atributo("tabelaAlternativo", "").ql();
		util.tab().atributo("larguraRotulos", false).ql();
		util.tab().atributo("checarRegistro", false).ql();
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
		util.tab().atributo("orderBy", "").ql();
		util.tab().atributo("tabelas", "").ql();
		util.tab().atributo("chaves", "").ql();
		util.tab().atributo("joins", "").ql();
		util.tab().atributo("sane", true).ql();
		util.tab().atributo("ccsc", true).ql();
		util.tab().atributo("bpnt", false).fecharTag();

		Instrucao i = new Instrucao("Resumo da instrucao");
		i.setValor("UPDATE candidato SET votos = 0 WHERE id = #id#");
		i.salvar(util, false);

		Filtro f = new Filtro("Resumo do filtro");
		f.setValor("AND id = AND descricao like '%maria%'");
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

	public String getAjustarAltura() {
		return ajustarAltura;
	}

	public void setAjustarAltura(String ajustarAltura) {
		this.ajustarAltura = ajustarAltura;
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

	public String getChecarRegistro() {
		return checarRegistro;
	}

	public void setTransparente(String transparente) {
		this.transparente = transparente;
	}

	public void setChecarRegistro(String checarRegistro) {
		this.checarRegistro = checarRegistro;
	}

	public String getClonarAoDestacar() {
		return clonarAoDestacar;
	}

	public void setClonarAoDestacar(String clonarAoDestacar) {
		this.clonarAoDestacar = clonarAoDestacar;
	}
}