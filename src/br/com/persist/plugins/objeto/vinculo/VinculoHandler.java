package br.com.persist.plugins.objeto.vinculo;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import br.com.persist.marca.XMLHandler;

class VinculoHandler extends XMLHandler {
	private final List<Pesquisa> pesquisas;
	private Pesquisa selecionado;

	public VinculoHandler() {
		pesquisas = new ArrayList<>();
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
		return ref;
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if ("pesquisa".equals(qName)) {
			selecionado = null;
		}
	}
}