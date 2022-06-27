package br.com.persist.plugins.checagem;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import br.com.persist.assistencia.Util;
import br.com.persist.marca.XMLHandler;

class ChecagemHandler extends XMLHandler {
	private final StringBuilder builder = new StringBuilder();
	private final List<Set> sentencas = new ArrayList<>();

	private void limpar() {
		if (builder.length() > 0) {
			builder.delete(0, builder.length());
		}
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if ("set".equals(qName)) {
			Set set = new Set(attributes.getValue("id"));
			sentencas.add(set);
			limpar();
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if ("set".equals(qName)) {
			String string = builder.toString();
			if (!Util.estaVazio(string)) {
				Set set = sentencas.get(sentencas.size() - 1);
				set.setString(string.trim());
			}
			limpar();
		}
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		builder.append(new String(ch, start, length));
	}

	public List<Set> getSentencas() {
		return sentencas;
	}
}