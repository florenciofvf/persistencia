package br.com.persist.plugins.atributo;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import br.com.persist.marca.XMLHandler;

class AtributoHandler extends XMLHandler {
	private final List<Atributo> atributos;

	AtributoHandler() {
		atributos = new ArrayList<>();
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (AtributoConstantes.ATRIBUTO.equals(qName)) {
			Atributo atributo = new Atributo();
			atributo.aplicar(attributes);
			atributos.add(atributo);
		}
	}
}