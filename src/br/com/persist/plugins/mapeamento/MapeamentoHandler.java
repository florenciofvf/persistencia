package br.com.persist.plugins.mapeamento;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import br.com.persist.util.Constantes;
import br.com.persist.util.Util;
import br.com.persist.xml.XMLHandler;

class MapeamentoHandler extends XMLHandler {
	private final StringBuilder builder = new StringBuilder();
	private Mapeamento selecionado;

	private void limpar() {
		if (builder.length() > 0) {
			builder.delete(0, builder.length());
		}
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (Constantes.MAPEAMENTO.equals(qName)) {
			selecionado = new Mapeamento(attributes.getValue("nome"));
			MapeamentoProvedor.adicionar(selecionado);
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (Constantes.MAPEAMENTO.equals(qName)) {
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