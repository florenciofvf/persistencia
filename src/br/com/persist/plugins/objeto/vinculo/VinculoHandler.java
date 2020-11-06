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
	private final Map<String, List<Instrucao>> instrucoes;
	private final List<Pesquisa> pesquisas;
	private Pesquisa selecionado;

	public VinculoHandler() {
		pesquisas = new ArrayList<>();
		instrucoes = new HashMap<>();
	}

	public Map<String, List<Instrucao>> getInstrucoes() {
		return instrucoes;
	}

	public List<Pesquisa> getPesquisas() {
		return pesquisas;
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if ("pesquisa".equals(qName)) {
			selecionado = new Pesquisa(attributes.getValue("nome"), criar(attributes));
			pesquisas.add(selecionado);
		} else if ("ref".equals(qName) && selecionado != null) {
			selecionado.add(criar(attributes));
		}
	}

	private Referencia criar(Attributes attributes) {
		Referencia ref = new Referencia(attributes.getValue("grupo"), attributes.getValue("tabela"),
				attributes.getValue("campo"));
		ref.setVazioInvisivel("invisivel".equalsIgnoreCase(attributes.getValue("vazio")));
		String limparApos = attributes.getValue("limparApos");
		ref.setLimparApos(Boolean.parseBoolean(limparApos));
		ref.setCorFonte(getCorFonte(attributes));
		return ref;
	}

	private Color getCorFonte(Attributes attributes) {
		String corFonte = attributes.getValue("corFonte");
		if (!Util.estaVazio(corFonte)) {
			return Color.decode(corFonte);
		}
		return null;
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if ("pesquisa".equals(qName)) {
			selecionado = null;
		}
	}
}