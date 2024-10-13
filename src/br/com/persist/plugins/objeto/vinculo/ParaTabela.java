package br.com.persist.plugins.objeto.vinculo;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;

import br.com.persist.assistencia.AssistenciaException;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Util;
import br.com.persist.marca.XMLUtil;
import br.com.persist.plugins.objeto.Objeto;
import br.com.persist.plugins.objeto.ObjetoException;

public class ParaTabela {
	private static final String ESQUEMA_ALTERNATIVO = "esquemaAlternativo";
	private static final String PREFIXO_NOME_TABELA = "prefixoNomeTabela";
	private static final String SELECT_ALTERNATIVO = "selectAlternativo";
	private static final String TABELA_ALTERNATIVO = "tabelaAlternativo";
	private static final String CLONAR_AO_DESTACAR = "clonarAoDestacar";
	private static final String BIBLIO_CHECAGEM = "biblioChecagem";
	private static final String LARGURA_ROTULOS = "larguraRotulos";
	private static final String AJUSTAR_LARGURA = "ajustarLargura";
	private static final String STR_TRANSPARENTE = "transparente";
	private static final String INTERNAL_FORM_X = "internalFormX";
	private static final String FINAL_CONSULTA = "finalConsulta";
	private static final String AJUSTAR_ALTURA = "ajustarAltura";
	private static final String STR_COMPLEMENTO = "complemento";
	private static final String STR_DESTACAVEIS = "destacaveis";
	private static final String STR_DESENHAR_ID = "desenharId";
	private static final String STR_SEQUENCIAS = "sequencias";
	private static final String STR_MAPEAMENTO = "mapeamento";
	private static final String STR_DESTACAVEL = "destacavel";
	private static final String CLASS_BIBLIO = "classBiblio";
	private static final String STR_INTERVALO = "intervalo";
	private static final String CAMPO_NOMES = "campoNomes";
	private static final String COLUNA_INFO = "colunaInfo";
	private static final String DESLOC_X_ID = "deslocXId";
	private static final String DESLOC_Y_ID = "deslocYId";
	private static final String STR_TABELAS = "tabelas";
	private static final String STR_APELIDO = "apelido";
	private static final String STR_ARQUIVO = "arquivo";
	private static final String STR_IGNORAR = "ignorar";
	private static final String LINK_AUTO = "linkAuto";
	private static final String STR_CHAVES = "chaves";
	public static final String COR_FONTE = "corFonte";
	public static final String COR_FUNDO = "corFundo";
	private static final String ORDER_BY = "orderBy";
	private static final String STR_JOINS = "joins";
	private static final String STR_CCSC = "ccsc";
	private static final String STR_SANE = "sane";
	private static final String STR_BPNT = "bpnt";
	private static final String STR_ID = "id";
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
	private String internalFormX;
	private String transparente;
	private String complemento;
	private String classBiblio;
	private String destacaveis;
	private String desenharId;
	private String colunaInfo;
	private String destacavel;
	private String sequencias;
	private String campoNomes;
	private String mapeamento;
	private String intervalo;
	private String deslocXId;
	private String deslocYId;
	private String linkAuto;
	private String tabelas;
	private String apelido;
	private String arquivo;
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
	private String id;

	public ParaTabela(String tabela) throws ObjetoException {
		if (Util.isEmpty(tabela)) {
			throw new ObjetoException("Tabela vazia.");
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

	public void addInstrucao(String instrucao, Marcador marcador) throws ObjetoException {
		if (marcador != null) {
			marcador.aplicarIf(instrucoes.isEmpty() ? "" : ".");
			return;
		}
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

	public void addFiltro(String filtro, Marcador marcador) throws ObjetoException {
		if (marcador != null) {
			marcador.aplicarIf(filtros.isEmpty() ? "" : ".");
			return;
		}
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

	public void config(Objeto objeto) throws AssistenciaException {
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

	private void config2(Objeto objeto) throws AssistenciaException {
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

	private void config3(Objeto objeto) throws AssistenciaException {
		if (!Util.isEmpty(internalFormX)) {
			objeto.setInternalFormX(internalFormX);
		}
		if (!Util.isEmpty(chaves)) {
			objeto.setChaves(chaves);
		}
		if (!Util.isEmpty(joins)) {
			objeto.setJoins(joins);
		}
		if (!Util.isEmpty(grupo)) {
			objeto.setGrupo(grupo);
		}
		if (!Util.isEmpty(id)) {
			objeto.setId(id);
		}
		if (icone != null) {
			objeto.setIcone(icone);
		}
		if (!Util.isEmpty(arquivo)) {
			objeto.setArquivo(arquivo);
		}
		if (!Util.isEmpty(desenharId)) {
			objeto.setDesenharId(Boolean.parseBoolean(desenharId));
		}
		if (!Util.isEmpty(intervalo)) {
			objeto.setIntervalo(Integer.parseInt(intervalo));
		}
		if (!Util.isEmpty(deslocXId)) {
			objeto.setDeslocamentoXId(Integer.parseInt(deslocXId));
		}
		if (!Util.isEmpty(deslocYId)) {
			objeto.setDeslocamentoYId(Integer.parseInt(deslocYId));
		}
	}

	public void aplicar(Attributes attributes) {
		setEsquemaAlternativo(attributes.getValue(ESQUEMA_ALTERNATIVO), null);
		setPrefixoNomeTabela(attributes.getValue(PREFIXO_NOME_TABELA), null);
		setTabelaAlternativo(attributes.getValue(TABELA_ALTERNATIVO), null);
		setSelectAlternativo(attributes.getValue(SELECT_ALTERNATIVO), null);
		setClonarAoDestacar(attributes.getValue(CLONAR_AO_DESTACAR), null);
		setLarguraRotulos(attributes.getValue(LARGURA_ROTULOS), null);
		setBiblioChecagem(attributes.getValue(BIBLIO_CHECAGEM), null);
		setAjustarLargura(attributes.getValue(AJUSTAR_LARGURA), null);
		setTransparente(attributes.getValue(STR_TRANSPARENTE), null);
		setInternalFormX(attributes.getValue(INTERNAL_FORM_X), null);
		setFinalConsulta(attributes.getValue(FINAL_CONSULTA), null);
		setAjustarAltura(attributes.getValue(AJUSTAR_ALTURA), null);
		setComplemento(attributes.getValue(STR_COMPLEMENTO), null);
		setDestacaveis(attributes.getValue(STR_DESTACAVEIS), null);
		setDesenharId(attributes.getValue(STR_DESENHAR_ID), null);
		setIcone(attributes.getValue(VinculoHandler.ICONE));
		setGrupo(attributes.getValue(VinculoHandler.GRUPO), null);
		setMapeamento(attributes.getValue(STR_MAPEAMENTO), null);
		setSequencias(attributes.getValue(STR_SEQUENCIAS), null);
		setDestacavel(attributes.getValue(STR_DESTACAVEL), null);
		setClassBiblio(attributes.getValue(CLASS_BIBLIO), null);
		setIntervalo(attributes.getValue(STR_INTERVALO), null);
		setCampoNomes(attributes.getValue(CAMPO_NOMES), null);
		setColunaInfo(attributes.getValue(COLUNA_INFO), null);
		setDeslocXId(attributes.getValue(DESLOC_X_ID), null);
		setDeslocYId(attributes.getValue(DESLOC_Y_ID), null);
		setApelido(attributes.getValue(STR_APELIDO), null);
		setArquivo(attributes.getValue(STR_ARQUIVO), null);
		setTabelas(attributes.getValue(STR_TABELAS), null);
		setIgnorar(attributes.getValue(STR_IGNORAR), null);
		setLinkAuto(attributes.getValue(LINK_AUTO), null);
		setChaves(attributes.getValue(STR_CHAVES), null);
		setOrderBy(attributes.getValue(ORDER_BY), null);
		setJoins(attributes.getValue(STR_JOINS), null);
		setSane(attributes.getValue(STR_SANE), null);
		setCcsc(attributes.getValue(STR_CCSC), null);
		setBpnt(attributes.getValue(STR_BPNT), null);
		setId(attributes.getValue(STR_ID), null);
		setCorFonte(getCorFonte(attributes));
		setCorFundo(getCorFundo(attributes));
	}

	public void salvar(XMLUtil util, boolean ql) {
		if (ql) {
			util.ql();
		}
		util.abrirTag(VinculoHandler.PARA);
		atributoValor(util, VinculoHandler.TABELA, tabela);
		atributoValor(util, VinculoHandler.ICONE, icone);
		if (corFonte != null) {
			atributoValor(util, COR_FONTE, Referencia.toHex(corFonte));
		}
		if (corFundo != null) {
			atributoValor(util, COR_FUNDO, Referencia.toHex(corFundo));
		}
		atributoValor(util, ESQUEMA_ALTERNATIVO, esquemaAlternativo);
		atributoValor(util, PREFIXO_NOME_TABELA, prefixoNomeTabela);
		atributoValor(util, TABELA_ALTERNATIVO, tabelaAlternativo);
		atributoValor(util, SELECT_ALTERNATIVO, selectAlternativo);
		atributoValor(util, CLONAR_AO_DESTACAR, clonarAoDestacar);
		atributoValor(util, LARGURA_ROTULOS, larguraRotulos);
		atributoValor(util, BIBLIO_CHECAGEM, biblioChecagem);
		atributoValor(util, AJUSTAR_LARGURA, ajustarLargura);
		atributoValor(util, INTERNAL_FORM_X, internalFormX);
		atributoValor(util, STR_TRANSPARENTE, transparente);
		atributoValor(util, FINAL_CONSULTA, finalConsulta);
		atributoValor(util, AJUSTAR_ALTURA, ajustarAltura);
		atributoValor(util, STR_COMPLEMENTO, complemento);
		atributoValor(util, STR_DESTACAVEIS, destacaveis);
		atributoValor(util, VinculoHandler.GRUPO, grupo);
		atributoValor(util, STR_DESENHAR_ID, desenharId);
		atributoValor(util, STR_MAPEAMENTO, mapeamento);
		atributoValor(util, STR_SEQUENCIAS, sequencias);
		atributoValor(util, STR_DESTACAVEL, destacavel);
		atributoValor(util, CLASS_BIBLIO, classBiblio);
		atributoValor(util, STR_INTERVALO, intervalo);
		atributoValor(util, CAMPO_NOMES, campoNomes);
		atributoValor(util, COLUNA_INFO, colunaInfo);
		atributoValor(util, DESLOC_X_ID, deslocXId);
		atributoValor(util, DESLOC_Y_ID, deslocYId);
		atributoValor(util, STR_APELIDO, apelido);
		atributoValor(util, STR_ARQUIVO, arquivo);
		atributoValor(util, STR_TABELAS, tabelas);
		atributoValor(util, STR_IGNORAR, ignorar);
		atributoValor(util, LINK_AUTO, linkAuto);
		atributoValor(util, STR_CHAVES, chaves);
		atributoValor(util, ORDER_BY, orderBy);
		atributoValor(util, STR_JOINS, joins);
		atributoValor(util, STR_SANE, sane);
		atributoValor(util, STR_CCSC, ccsc);
		atributoValor(util, STR_BPNT, bpnt);
		atributoValor(util, STR_ID, id);
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

	public void modelo(XMLUtil util) throws ObjetoException {
		util.abrirTag(VinculoHandler.PARA).atributo(VinculoHandler.TABELA, VinculoHandler.NOME_TABELA)
				.atributo(VinculoHandler.ICONE, "nome_icone").atributo(COR_FONTE, "#AABBCC")
				.atributo(COR_FUNDO, "#CAFEBB").ql();
		util.tab().atributo(ESQUEMA_ALTERNATIVO, "").ql();
		util.tab().atributo(PREFIXO_NOME_TABELA, "H_").ql();
		util.tab().atributo(TABELA_ALTERNATIVO, "").ql();
		util.tab().atributo(SELECT_ALTERNATIVO, "").ql();
		util.tab().atributo(CLONAR_AO_DESTACAR, true).ql();
		util.tab().atributo(LARGURA_ROTULOS, false).ql();
		util.tab().atributo(BIBLIO_CHECAGEM, "").ql();
		util.tab().atributo(AJUSTAR_LARGURA, true).ql();
		util.tab().atributo(INTERNAL_FORM_X, "").ql();
		util.tab().atributo(STR_TRANSPARENTE, false).ql();
		util.tab().atributo(FINAL_CONSULTA, "").ql();
		util.tab().atributo(AJUSTAR_ALTURA, true).ql();
		util.tab().atributo(STR_COMPLEMENTO, "").ql();
		util.tab().atributo(STR_DESTACAVEIS, "").ql();
		util.tab().atributo(VinculoHandler.GRUPO, "").ql();
		util.tab().atributo(STR_DESENHAR_ID, true).ql();
		util.tab().atributo(STR_MAPEAMENTO, "").ql();
		util.tab().atributo(STR_SEQUENCIAS, "").ql();
		util.tab().atributo(STR_DESTACAVEL, true).ql();
		util.tab().atributo(CLASS_BIBLIO, "").ql();
		util.tab().atributo(STR_INTERVALO, "").ql();
		util.tab().atributo(CAMPO_NOMES, "").ql();
		util.tab().atributo(COLUNA_INFO, false).ql();
		util.tab().atributo(DESLOC_X_ID, "").ql();
		util.tab().atributo(DESLOC_Y_ID, "").ql();
		util.tab().atributo(STR_APELIDO, "ape").ql();
		util.tab().atributo(STR_ARQUIVO, "").ql();
		util.tab().atributo(STR_TABELAS, "").ql();
		util.tab().atributo(STR_IGNORAR, false).ql();
		util.tab().atributo(LINK_AUTO, true).ql();
		util.tab().atributo(STR_CHAVES, "").ql();
		util.tab().atributo(ORDER_BY, "").ql();
		util.tab().atributo(STR_JOINS, "").ql();
		util.tab().atributo(STR_SANE, true).ql();
		util.tab().atributo(STR_CCSC, true).ql();
		util.tab().atributo(STR_BPNT, false).ql();
		util.tab().atributo(STR_ID, "").fecharTag();

		boolean ql = false;
		Instrucao i = new Instrucao("Resumo da instrucao");
		i.setValor("UPDATE candidato SET votos = 0 WHERE id = " + Constantes.SEP + "id" + Constantes.SEP);
		i.salvar(util, ql);
		ql = true;

		Filtro f = new Filtro("Resumo do filtro");
		f.setValor("AND id = 1 AND descricao LIKE '%Descricao%'");
		f.salvar(util, ql);

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

	public void setApelido(String apelido, Marcador marcador) {
		if (marcador != null) {
			marcador.aplicarIf(this.apelido);
			return;
		}
		this.apelido = apelido;
	}

	public String getArquivo() {
		return arquivo;
	}

	public void setArquivo(String arquivo, Marcador marcador) {
		if (marcador != null) {
			marcador.aplicarIf(this.arquivo);
			return;
		}
		this.arquivo = arquivo;
	}

	public String getSequencias() {
		return sequencias;
	}

	public void setSequencias(String sequencias, Marcador marcador) {
		if (marcador != null) {
			marcador.aplicarIf(this.sequencias);
			return;
		}
		this.sequencias = sequencias;
	}

	public String getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(String orderBy, Marcador marcador) {
		if (marcador != null) {
			marcador.aplicarIf(this.orderBy);
			return;
		}
		this.orderBy = orderBy;
	}

	public String getPrefixoNomeTabela() {
		return prefixoNomeTabela;
	}

	public void setPrefixoNomeTabela(String prefixoNomeTabela, Marcador marcador) {
		if (marcador != null) {
			marcador.aplicarIf(this.prefixoNomeTabela);
			return;
		}
		this.prefixoNomeTabela = prefixoNomeTabela;
	}

	public String getSelectAlternativo() {
		return selectAlternativo;
	}

	public void setSelectAlternativo(String selectAlternativo, Marcador marcador) {
		if (marcador != null) {
			marcador.aplicarIf(this.selectAlternativo);
			return;
		}
		this.selectAlternativo = selectAlternativo;
	}

	public String getFinalConsulta() {
		return finalConsulta;
	}

	public void setFinalConsulta(String finalConsulta, Marcador marcador) {
		if (marcador != null) {
			marcador.aplicarIf(this.finalConsulta);
			return;
		}
		this.finalConsulta = finalConsulta;
	}

	public String getComplemento() {
		return complemento;
	}

	public void setComplemento(String complemento, Marcador marcador) {
		if (marcador != null) {
			marcador.aplicarIf(this.complemento);
			return;
		}
		this.complemento = complemento;
	}

	public String getClassBiblio() {
		return classBiblio;
	}

	public void setClassBiblio(String classBiblio, Marcador marcador) {
		if (marcador != null) {
			marcador.aplicarIf(this.classBiblio);
			return;
		}
		this.classBiblio = classBiblio;
	}

	public String getDestacaveis() {
		return destacaveis;
	}

	public void setDestacaveis(String destacaveis, Marcador marcador) {
		if (marcador != null) {
			marcador.aplicarIf(this.destacaveis);
			return;
		}
		this.destacaveis = destacaveis;
	}

	public String getCampoNomes() {
		return campoNomes;
	}

	public void setCampoNomes(String campoNomes, Marcador marcador) {
		if (marcador != null) {
			marcador.aplicarIf(this.campoNomes);
			return;
		}
		this.campoNomes = campoNomes;
	}

	public String getMapeamento() {
		return mapeamento;
	}

	public void setMapeamento(String mapeamento, Marcador marcador) {
		if (marcador != null) {
			marcador.aplicarIf(this.mapeamento);
			return;
		}
		this.mapeamento = mapeamento;
	}

	public String getChaves() {
		return chaves;
	}

	public void setChaves(String chaves, Marcador marcador) {
		if (marcador != null) {
			marcador.aplicarIf(this.chaves);
			return;
		}
		this.chaves = chaves;
	}

	public String getGrupo() {
		return grupo;
	}

	public void setGrupo(String grupo, Marcador marcador) {
		if (marcador != null) {
			marcador.aplicarIf(this.grupo);
			return;
		}
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

	public void setAjustarAltura(String ajustarAltura, Marcador marcador) {
		if (marcador != null) {
			marcador.aplicarIf(this.ajustarAltura);
			return;
		}
		this.ajustarAltura = ajustarAltura;
	}

	public String getAjustarLargura() {
		return ajustarLargura;
	}

	public void setAjustarLargura(String ajustarLargura, Marcador marcador) {
		if (marcador != null) {
			marcador.aplicarIf(this.ajustarLargura);
			return;
		}
		this.ajustarLargura = ajustarLargura;
	}

	public String getColunaInfo() {
		return colunaInfo;
	}

	public void setColunaInfo(String colunaInfo, Marcador marcador) {
		if (marcador != null) {
			marcador.aplicarIf(this.colunaInfo);
			return;
		}
		this.colunaInfo = colunaInfo;
	}

	public String getDestacavel() {
		return destacavel;
	}

	public void setDestacavel(String destacavel, Marcador marcador) {
		if (marcador != null) {
			marcador.aplicarIf(this.destacavel);
			return;
		}
		this.destacavel = destacavel;
	}

	public String getLinkAuto() {
		return linkAuto;
	}

	public void setLinkAuto(String linkAuto, Marcador marcador) {
		if (marcador != null) {
			marcador.aplicarIf(this.linkAuto);
			return;
		}
		this.linkAuto = linkAuto;
	}

	public String getLarguraRotulos() {
		return larguraRotulos;
	}

	public void setLarguraRotulos(String larguraRotulos, Marcador marcador) {
		if (marcador != null) {
			marcador.aplicarIf(this.larguraRotulos);
			return;
		}
		this.larguraRotulos = larguraRotulos;
	}

	public String getSane() {
		return sane;
	}

	public void setSane(String sane, Marcador marcador) {
		if (marcador != null) {
			marcador.aplicarIf(this.sane);
			return;
		}
		this.sane = sane;
	}

	public String getIgnorar() {
		return ignorar;
	}

	public void setIgnorar(String ignorar, Marcador marcador) {
		if (marcador != null) {
			marcador.aplicarIf(this.ignorar);
			return;
		}
		this.ignorar = ignorar;
	}

	public String getCcsc() {
		return ccsc;
	}

	public void setCcsc(String ccsc, Marcador marcador) {
		if (marcador != null) {
			marcador.aplicarIf(this.ccsc);
			return;
		}
		this.ccsc = ccsc;
	}

	public String getBpnt() {
		return bpnt;
	}

	public void setBpnt(String bpnt, Marcador marcador) {
		if (marcador != null) {
			marcador.aplicarIf(this.bpnt);
			return;
		}
		this.bpnt = bpnt;
	}

	public String getId() {
		return id;
	}

	public void setId(String id, Marcador marcador) {
		if (marcador != null) {
			marcador.aplicarIf(this.id);
			return;
		}
		this.id = id;
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

	public void setTabelas(String tabelas, Marcador marcador) {
		if (marcador != null) {
			marcador.aplicarIf(this.tabelas);
			return;
		}
		this.tabelas = tabelas;
	}

	public String getEsquemaAlternativo() {
		return esquemaAlternativo;
	}

	public void setEsquemaAlternativo(String esquemaAlternativo, Marcador marcador) {
		if (marcador != null) {
			marcador.aplicarIf(this.esquemaAlternativo);
			return;
		}
		this.esquemaAlternativo = esquemaAlternativo;
	}

	public String getTabelaAlternativo() {
		return tabelaAlternativo;
	}

	public void setTabelaAlternativo(String tabelaAlternativo, Marcador marcador) {
		if (marcador != null) {
			marcador.aplicarIf(this.tabelaAlternativo);
			return;
		}
		this.tabelaAlternativo = tabelaAlternativo;
	}

	public String getJoins() {
		return joins;
	}

	public void setJoins(String joins, Marcador marcador) {
		if (marcador != null) {
			marcador.aplicarIf(this.joins);
			return;
		}
		this.joins = joins;
	}

	public String getInternalFormX() {
		return internalFormX;
	}

	public void setInternalFormX(String internalFormX, Marcador marcador) {
		if (marcador != null) {
			marcador.aplicarIf(this.internalFormX);
			return;
		}
		this.internalFormX = internalFormX;
	}

	public String getTransparente() {
		return transparente;
	}

	public String getBiblioChecagem() {
		return biblioChecagem;
	}

	public void setTransparente(String transparente, Marcador marcador) {
		if (marcador != null) {
			marcador.aplicarIf(this.transparente);
			return;
		}
		this.transparente = transparente;
	}

	public void setBiblioChecagem(String biblioChecagem, Marcador marcador) {
		if (marcador != null) {
			marcador.aplicarIf(this.biblioChecagem);
			return;
		}
		this.biblioChecagem = biblioChecagem;
	}

	public String getDesenharId() {
		return desenharId;
	}

	public void setDesenharId(String desenharId, Marcador marcador) {
		if (marcador != null) {
			marcador.aplicarIf(this.desenharId);
			return;
		}
		this.desenharId = desenharId;
	}

	public String getIntervalo() {
		return intervalo;
	}

	public void setIntervalo(String intervalo, Marcador marcador) {
		if (marcador != null) {
			marcador.aplicarIf(this.intervalo);
			return;
		}
		this.intervalo = intervalo;
	}

	public String getDeslocXId() {
		return deslocXId;
	}

	public void setDeslocXId(String deslocXId, Marcador marcador) {
		if (marcador != null) {
			marcador.aplicarIf(this.deslocXId);
			return;
		}
		this.deslocXId = deslocXId;
	}

	public String getDeslocYId() {
		return deslocYId;
	}

	public void setDeslocYId(String deslocYId, Marcador marcador) {
		if (marcador != null) {
			marcador.aplicarIf(this.deslocYId);
			return;
		}
		this.deslocYId = deslocYId;
	}

	public String getClonarAoDestacar() {
		return clonarAoDestacar;
	}

	public void setClonarAoDestacar(String clonarAoDestacar, Marcador marcador) {
		if (marcador != null) {
			marcador.aplicarIf(this.clonarAoDestacar);
			return;
		}
		this.clonarAoDestacar = clonarAoDestacar;
	}

	private static Color getCorFonte(Attributes attributes) {
		String corFonte = attributes.getValue(COR_FONTE);
		if (!Util.isEmpty(corFonte)) {
			return Color.decode(corFonte);
		}
		return null;
	}

	private static Color getCorFundo(Attributes attributes) {
		String corFonte = attributes.getValue(COR_FUNDO);
		if (!Util.isEmpty(corFonte)) {
			return Color.decode(corFonte);
		}
		return null;
	}
}