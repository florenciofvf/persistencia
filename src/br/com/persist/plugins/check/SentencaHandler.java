package br.com.persist.plugins.check;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Util;
import br.com.persist.marca.XMLHandler;

class SentencaHandler extends XMLHandler {
	private final StringBuilder builder = new StringBuilder();
	private final SentencaColetor coletor;
	private Sentenca selecionado;

	SentencaHandler(SentencaColetor coletor) {
		this.coletor = coletor;
		coletor.init();
	}

	private void limpar() {
		if (builder.length() > 0) {
			builder.delete(0, builder.length());
		}
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (Constantes.SENTENCA.equals(qName)) {
			selecionado = new Sentenca();
			coletor.getSentencas().add(selecionado);
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (Constantes.SENTENCA.equals(qName)) {
			String string = builder.toString();
			if (!Util.estaVazio(string)) {
				selecionado.setString(string.trim());
			} else {
				throw new IllegalStateException();
			}
			limpar();
			selecionado = null;
		}
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		builder.append(new String(ch, start, length));
	}
}