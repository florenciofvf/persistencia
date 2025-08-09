package br.com.persist.plugins.navegacao;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import br.com.persist.marca.XMLHandler;

class NavegacaoXMLHandler extends XMLHandler {
	// private Navegacao selecionado;

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (NavegacaoConstantes.NAVEGACAO.equals(qName)) {
			// selecionado = new Navegacao(attributes.getValue("nome"));
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		// TODO - impl
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		// TODO - impl
	}
}