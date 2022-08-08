package br.com.persist.plugins.checagem;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import br.com.persist.marca.XMLHandler;

class ChecagemHandler extends XMLHandler {
	private final StringBuilder builder = new StringBuilder();
	private static final String BLOCO = "set";
	private final Modulo modulo;

	public ChecagemHandler(Modulo modulo) {
		this.modulo = modulo;
	}

	private void limpar() {
		if (builder.length() > 0) {
			builder.delete(0, builder.length());
		}
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (BLOCO.equals(qName)) {
			Bloco bloco = new Bloco(modulo, attributes.getValue("id"));
			boolean privado = Boolean.parseBoolean(attributes.getValue("privado"));
			bloco.setPrivado(privado);
			modulo.add(bloco);
			limpar();
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (BLOCO.equals(qName)) {
			String string = builder.toString();
			Bloco bloco = modulo.getUltimoBloco();
			bloco.setString(string);
			limpar();
		}
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		builder.append(new String(ch, start, length));
	}
}