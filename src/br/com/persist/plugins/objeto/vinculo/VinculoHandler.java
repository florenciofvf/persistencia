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
import br.com.persist.plugins.objeto.Instrucao;

class VinculoHandler extends XMLHandler {
	private final StringBuilder builder = new StringBuilder();
	private final Map<String, ParaTabela> mapaParaTabela;
	private static final String TABELA = "tabela";
	private static final String ICONE = "icone";
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
		if ("pesquisa".equals(qName)) {
			selecionado = new Pesquisa(attributes.getValue("nome"), criar(attributes));
			pesquisas.add(selecionado);
		} else if ("para".equals(qName)) {
			tabelaSelecionada = attributes.getValue(TABELA);
			if (!Util.estaVazio(tabelaSelecionada)) {
				mapaParaTabela.computeIfAbsent(tabelaSelecionada,
						t -> new ParaTabela(tabelaSelecionada, attributes.getValue(ICONE)));
			}
		} else if ("instrucao".equals(qName)) {
			processarInstrucao(attributes);
			limpar();
		} else if ("ref".equals(qName) && selecionado != null) {
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

	public static Referencia criar(Attributes attributes) {
		Referencia ref = new Referencia(attributes.getValue("grupo"), attributes.getValue(TABELA),
				attributes.getValue("campo"));
		ref.setVazioInvisivel("invisivel".equalsIgnoreCase(attributes.getValue("vazio")));
		String limparApos = attributes.getValue("limparApos");
		ref.setIconeGrupo(attributes.getValue("iconeGrupo"));
		ref.setLimparApos(Boolean.parseBoolean(limparApos));
		ref.setIcone(attributes.getValue(ICONE));
		ref.setCorFonte(getCorFonte(attributes));
		return ref;
	}

	public static Referencia criar(Map<String, String> attributes) {
		Referencia ref = new Referencia(attributes.get("grupo"), attributes.get(TABELA), attributes.get("campo"));
		ref.setVazioInvisivel("invisivel".equalsIgnoreCase(attributes.get("vazio")));
		String limparApos = attributes.get("limparApos");
		ref.setIconeGrupo(attributes.get("iconeGrupo"));
		ref.setLimparApos(Boolean.parseBoolean(limparApos));
		ref.setCorFonte(getCorFonte(attributes));
		ref.setIcone(attributes.get(ICONE));
		return ref;
	}

	private static Color getCorFonte(Attributes attributes) {
		String corFonte = attributes.getValue("corFonte");
		if (!Util.estaVazio(corFonte)) {
			return Color.decode(corFonte);
		}
		return null;
	}

	private static Color getCorFonte(Map<String, String> attributes) {
		String corFonte = attributes.get("corFonte");
		if (!Util.estaVazio(corFonte)) {
			return Color.decode(corFonte);
		}
		return null;
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if ("pesquisa".equals(qName)) {
			selecionado = null;
		} else if ("instrucao".equals(qName)) {
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