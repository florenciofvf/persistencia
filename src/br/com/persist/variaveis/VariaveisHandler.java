package br.com.persist.variaveis;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import br.com.persist.chave_valor.ChaveValor;
import br.com.persist.util.Constantes;
import br.com.persist.util.Util;
import br.com.persist.xml.XMLHandler;

class VariaveisHandler extends XMLHandler {
	private final StringBuilder builder = new StringBuilder();
	private ChaveValor selecionado;

	private void limpar() {
		if (builder.length() > 0) {
			builder.delete(0, builder.length());
		}
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (Constantes.CHAVE_VALOR.equals(qName)) {
			selecionado = new ChaveValor(Constantes.TEMP);
			selecionado.aplicar(attributes);
			VariaveisModelo.adicionar(selecionado);
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (Constantes.CHAVE_VALOR.equals(qName)) {
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