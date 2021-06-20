package br.com.persist.plugins.objeto.vinculo;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import br.com.persist.assistencia.Util;
import br.com.persist.marca.XMLHandler;
import br.com.persist.marca.XMLUtil;

public class VinculoHandler extends XMLHandler {
	private final StringBuilder builder = new StringBuilder();
	private final Map<String, ParaTabela> mapaParaTabela;
	private static final String NOME_TABELA = "NOME_TABELA";
	private static final String LIMPAR_APOS = "limparApos";
	private static final String ICONE_GRUPO = "iconeGrupo";
	private static final String INVISIVEL = "invisivel";
	private static final String INSTRUCAO = "instrucao";
	private static final String COR_FONTE = "corFonte";
	private static final String PESQUISA = "pesquisa";
	private static final String TABELA = "tabela";
	private static final String CAMPO = "campo";
	private static final String ICONE = "icone";
	private static final String GRUPO = "grupo";
	private static final String VAZIO = "vazio";
	private static final String REF = "ref";
	private final List<Pesquisa> pesquisas;
	private String tabelaSelecionada;
	private Pesquisa selecionado;

	public VinculoHandler() {
		mapaParaTabela = new HashMap<>();
		pesquisas = new ArrayList<>();
	}

	public Map<String, ParaTabela> getMapaParaTabela() {
		return mapaParaTabela;
	}

	public List<Pesquisa> getPesquisas() {
		return pesquisas;
	}

	private void limpar() {
		if (builder.length() > 0) {
			builder.delete(0, builder.length());
		}
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (PESQUISA.equals(qName)) {
			selecionado = new Pesquisa(attributes.getValue("nome"), criar(attributes));
			pesquisas.add(selecionado);
		} else if ("para".equals(qName)) {
			tabelaSelecionada = attributes.getValue(TABELA);
			if (!Util.estaVazio(tabelaSelecionada)) {
				mapaParaTabela.computeIfAbsent(tabelaSelecionada, t -> criarParaTabela(tabelaSelecionada, attributes));
			}
		} else if (INSTRUCAO.equals(qName)) {
			processarInstrucao(attributes);
			limpar();
		} else if (REF.equals(qName) && selecionado != null) {
			selecionado.add(criar(attributes));
		}
	}

	private void processarInstrucao(Attributes attributes) {
		ParaTabela paraTabela = mapaParaTabela.get(tabelaSelecionada);
		if (paraTabela != null) {
			addInstrucao(attributes, paraTabela.getInstrucoes());
		}
	}

	private void addInstrucao(Attributes attributes, List<Instrucao> lista) {
		Instrucao i = new Instrucao(attributes.getValue("nome"));
		boolean sm = Boolean.parseBoolean(attributes.getValue("selecaoMultipla"));
		String ordem = attributes.getValue("ordem");
		if (!Util.estaVazio(ordem)) {
			i.setOrdem(Integer.parseInt(ordem));
		}
		i.setSelecaoMultipla(sm);
		lista.add(i);
	}

	private static ParaTabela criarParaTabela(String tabela, Attributes attributes) {
		ParaTabela paraTabela = new ParaTabela(tabela, attributes.getValue(ICONE), getCorFonte(attributes));
		paraTabela.setPrefixoNomeTabela(attributes.getValue("prefixoNomeTabela"));
		paraTabela.setSelectAlternativo(attributes.getValue("selectAlternativo"));
		paraTabela.setFinalConsulta(attributes.getValue("finalConsulta"));
		paraTabela.setAjustarAltura(attributes.getValue("ajustarAltura"));
		paraTabela.setComplemento(attributes.getValue("complemento"));
		paraTabela.setMapeamento(attributes.getValue("mapeamento"));
		paraTabela.setSequencias(attributes.getValue("sequencias"));
		paraTabela.setCampoNomes(attributes.getValue("campoNomes"));
		paraTabela.setColunaInfo(attributes.getValue("colunaInfo"));
		paraTabela.setDestacavel(attributes.getValue("destacavel"));
		paraTabela.setLinkAuto(attributes.getValue("linkAuto"));
		paraTabela.setApelido(attributes.getValue("apelido"));
		paraTabela.setOrderBy(attributes.getValue("orderBy"));
		paraTabela.setChaves(attributes.getValue("chaves"));
		paraTabela.setGrupo(attributes.getValue(GRUPO));
		paraTabela.setSane(attributes.getValue("sane"));
		paraTabela.setCcsc(attributes.getValue("ccsc"));
		paraTabela.setBpnt(attributes.getValue("bpnt"));
		return paraTabela;
	}

	public static void paraTabela(XMLUtil util) {
		util.abrirTag("para").atributo(TABELA, NOME_TABELA).atributo(ICONE, "nome_icone").atributo(COR_FONTE, "#FFVVFF")
				.ql();
		util.tab().atributo("prefixoNomeTabela", "H_").ql();
		util.tab().atributo("selectAlternativo", "").ql();
		util.tab().atributo("finalConsulta", "").ql();
		util.tab().atributo("ajustarAltura", true).ql();
		util.tab().atributo("complemento", "").ql();
		util.tab().atributo("mapeamento", "").ql();
		util.tab().atributo("sequencias", "").ql();
		util.tab().atributo("campoNomes", "").ql();
		util.tab().atributo("colunaInfo", false).ql();
		util.tab().atributo("destacavel", true).ql();
		util.tab().atributo("linkAuto", true).ql();
		util.tab().atributo("apelido", "ape").ql();
		util.tab().atributo("orderBy", "").ql();
		util.tab().atributo("chaves", "").ql();
		util.tab().atributo(GRUPO, "").ql();
		util.tab().atributo("sane", true).ql();
		util.tab().atributo("ccsc", true).ql();
		util.tab().atributo("bpnt", false).fecharTag();
		util.abrirTag(INSTRUCAO);
		util.atributo("nome", "Resumo da instrucao");
		util.atributo("selecaoMultipla", "false");
		util.atributo("ordem", "0").fecharTag();
		util.conteudo("<![CDATA[").ql();
		util.tab().conteudo("UPDATE candidato SET votos = 0 WHERE id = #id#").ql();
		util.conteudo("]]>").ql();
		util.finalizarTag(INSTRUCAO);
		util.finalizarTag("para");
	}

	public static void pesquisa(XMLUtil util) {
		util.ql();
		util.abrirTag(PESQUISA).atributo("nome", "Nome da pesquisa").atributo(TABELA, NOME_TABELA).atributo(CAMPO, "PK")
				.atributo(GRUPO, "").atributo(ICONE_GRUPO, "").atributo(ICONE, "").atributo(COR_FONTE, "#AABBCC")
				.fecharTag();
		util.abrirTag(REF).atributo(TABELA, NOME_TABELA).atributo(CAMPO, "FK").atributo(GRUPO, "")
				.atributo(VAZIO, INVISIVEL).atributo(ICONE, "").atributo(COR_FONTE, "#AABBCC").fecharTag2(-1);
		util.abrirTag(REF).atributo(TABELA, NOME_TABELA).atributo(LIMPAR_APOS, true).fecharTag2(-1);
		util.finalizarTag(PESQUISA);
	}

	public static Referencia criar(Attributes attributes) {
		Referencia ref = new Referencia(attributes.getValue(GRUPO), attributes.getValue(TABELA),
				attributes.getValue(CAMPO));
		ref.setVazioInvisivel(INVISIVEL.equalsIgnoreCase(attributes.getValue(VAZIO)));
		String limparApos = attributes.getValue(LIMPAR_APOS);
		ref.setIconeGrupo(attributes.getValue(ICONE_GRUPO));
		ref.setLimparApos(Boolean.parseBoolean(limparApos));
		ref.setIcone(attributes.getValue(ICONE));
		ref.setCorFonte(getCorFonte(attributes));
		return ref;
	}

	public static Referencia criar(Map<String, String> attributes) {
		Referencia ref = new Referencia(attributes.get(GRUPO), attributes.get(TABELA), attributes.get(CAMPO));
		ref.setVazioInvisivel(INVISIVEL.equalsIgnoreCase(attributes.get(VAZIO)));
		String limparApos = attributes.get(LIMPAR_APOS);
		ref.setIconeGrupo(attributes.get(ICONE_GRUPO));
		ref.setLimparApos(Boolean.parseBoolean(limparApos));
		ref.setCorFonte(getCorFonte(attributes));
		ref.setIcone(attributes.get(ICONE));
		return ref;
	}

	private static Color getCorFonte(Attributes attributes) {
		String corFonte = attributes.getValue(COR_FONTE);
		if (!Util.estaVazio(corFonte)) {
			return Color.decode(corFonte);
		}
		return null;
	}

	private static Color getCorFonte(Map<String, String> attributes) {
		String corFonte = attributes.get(COR_FONTE);
		if (!Util.estaVazio(corFonte)) {
			return Color.decode(corFonte);
		}
		return null;
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (PESQUISA.equals(qName)) {
			selecionado = null;
		} else if (INSTRUCAO.equals(qName)) {
			ParaTabela paraTabela = mapaParaTabela.get(tabelaSelecionada);
			if (paraTabela != null && !paraTabela.getInstrucoes().isEmpty()) {
				setValorInstrucao(paraTabela.getInstrucoes());
			}
			limpar();
		}
	}

	private void setValorInstrucao(List<Instrucao> lista) {
		Instrucao instrucao = lista.get(lista.size() - 1);
		String string = builder.toString();
		if (!Util.estaVazio(string)) {
			instrucao.setValor(string.trim());
		}
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		builder.append(new String(ch, start, length));
	}
}