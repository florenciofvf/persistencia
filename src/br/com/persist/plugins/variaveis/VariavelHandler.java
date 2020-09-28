package br.com.persist.plugins.variaveis;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Util;
import br.com.persist.marca.XMLHandler;

class VariavelHandler extends XMLHandler {
	private final StringBuilder builder = new StringBuilder();
	private Variavel selecionado;

	private void limpar() {
		if (builder.length() > 0) {
			builder.delete(0, builder.length());
		}
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (Constantes.VARIAVEL.equals(qName)) {
			selecionado = new Variavel(attributes.getValue("nome"));
			VariavelProvedor.adicionar(selecionado);
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (Constantes.VARIAVEL.equals(qName)) {
			selecionado = null;
		} else if (Constantes.VALOR.equals(qName) && selecionado != null) {
			String string = builder.toString();
			if (!Util.estaVazio(string)) {
				selecionado.setValor(string.trim());
			}
			limpar();
		}
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		builder.append(new String(ch, start, length));
	}
}