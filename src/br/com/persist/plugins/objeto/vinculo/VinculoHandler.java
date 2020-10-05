package br.com.persist.plugins.objeto.vinculo;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import br.com.persist.marca.XMLHandler;

class VinculoHandler extends XMLHandler {
	private final List<Grupo> grupos;
	private Grupo selecionado;

	public VinculoHandler() {
		grupos = new ArrayList<>();
	}

	public List<Grupo> getGrupos() {
		return grupos;
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if ("grupo".equals(qName)) {
			selecionado = new Grupo(attributes.getValue("nome"), criar(attributes));
			grupos.add(selecionado);
		} else if ("ref".equals(qName) && selecionado != null) {
			selecionado.add(criar(attributes));
		}
	}

	private Referencia criar(Attributes attributes) {
		return new Referencia(attributes.getValue("apelido"), attributes.getValue("tabela"),
				attributes.getValue("campo"));
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if ("grupo".equals(qName)) {
			selecionado = null;
		}
	}
}