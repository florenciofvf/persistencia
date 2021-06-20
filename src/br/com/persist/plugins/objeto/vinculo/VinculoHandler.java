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

public class VinculoHandler extends XMLHandler {
	private final StringBuilder builder = new StringBuilder();
	public static final String NOME_TABELA = "NOME_TABELA";
	public static final String LIMPAR_APOS = "limparApos";
	public static final String ICONE_GRUPO = "iconeGrupo";
	private final Map<String, ParaTabela> mapaParaTabela;
	public static final String INVISIVEL = "invisivel";
	public static final String INSTRUCAO = "instrucao";
	public static final String COR_FONTE = "corFonte";
	public static final String PESQUISA = "pesquisa";
	public static final String TABELA = "tabela";
	public static final String CAMPO = "campo";
	public static final String ICONE = "icone";
	public static final String GRUPO = "grupo";
	public static final String VAZIO = "vazio";
	public static final String NOME = "nome";
	public static final String PARA = "para";
	public static final String REF = "ref";
	public final List<Pesquisa> pesquisas;
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
			selecionado = new Pesquisa(attributes.getValue(NOME), criar(attributes));
			pesquisas.add(selecionado);
		} else if (PARA.equals(qName)) {
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
		Instrucao i = new Instrucao(attributes.getValue(NOME));
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