package br.com.persist.plugins.fragmento;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import br.com.persist.assistencia.ArgumentoException;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Util;
import br.com.persist.marca.XMLHandler;

class FragmentoHandler extends XMLHandler {
	private final StringBuilder builder = new StringBuilder();
	private Fragmento selecionado;

	private void limpar() {
		if (builder.length() > 0) {
			builder.delete(0, builder.length());
		}
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (FragmentoConstantes.FRAGMENTO.equals(qName)) {
			try {
				selecionado = new Fragmento(attributes.getValue("resumo"), attributes.getValue("grupo"));
				FragmentoProvedor.adicionar(selecionado);
			} catch (ArgumentoException ex) {
				throw new SAXException(ex);
			}
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (FragmentoConstantes.FRAGMENTO.equals(qName)) {
			selecionado = null;
		} else if (Constantes.VALOR.equals(qName) && selecionado != null) {
			String string = builder.toString();
			if (!Util.isEmpty(string)) {
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