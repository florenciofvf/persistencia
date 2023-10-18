package br.com.persist.plugins.propriedade;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import br.com.persist.marca.XMLHandler;

class PropriedadeHandler extends XMLHandler {
	//private Propriedade selecionado;

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (PropriedadeConstantes.PROPRIEDADE.equals(qName)) {
			//selecionado = new Propriedade(attributes.getValue("nome"));
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
	}
}