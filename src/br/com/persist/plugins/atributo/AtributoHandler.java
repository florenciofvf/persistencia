package br.com.persist.plugins.atributo;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import br.com.persist.marca.XMLHandler;

class AtributoHandler extends XMLHandler {
	//private Atributo selecionado;

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (AtributoConstantes.ATRIBUTO.equals(qName)) {
			//selecionado = new Atributo(attributes.getValue("nome"));
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
	}
}