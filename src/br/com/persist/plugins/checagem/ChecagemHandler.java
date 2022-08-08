package br.com.persist.plugins.checagem;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import br.com.persist.assistencia.Constantes;
import br.com.persist.marca.XMLHandler;

class ChecagemHandler extends XMLHandler {
	private static final String BLOCO = "set";
	private final Modulo modulo;

	public ChecagemHandler(Modulo modulo) {
		this.modulo = modulo;
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (BLOCO.equals(qName)) {
			Bloco bloco = new Bloco(modulo, attributes.getValue("id"));
			boolean privado = Boolean.parseBoolean(attributes.getValue("privado"));
			bloco.setPrivado(privado);
			modulo.add(bloco);
		}
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		Bloco bloco = modulo.getUltimoBloco();
		if (bloco != null) {
			bloco.append(new String(ch, start, length));
		}
	}

	@Override
	public void startCDATA() throws SAXException {
		Bloco bloco = modulo.getUltimoBloco();
		if (bloco != null) {
			bloco.append("<![CDATA[");
			bloco.setParaPre(false);
			bloco.setParaString(true);
		}
	}

	@Override
	public void endCDATA() throws SAXException {
		Bloco bloco = modulo.getUltimoBloco();
		if (bloco != null) {
			bloco.setParaPre(false);
			bloco.setParaString(false);
			bloco.setParaPos(true);
			bloco.append("]]>" + Constantes.QL);
			bloco.setParaPos(false);
		}
	}
}