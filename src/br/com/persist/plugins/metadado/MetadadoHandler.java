package br.com.persist.plugins.metadado;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import br.com.persist.marca.XMLHandler;

class MetadadoHandler extends XMLHandler {
	private Metadado selecionado;
	private Metadado raiz;

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (MetadadoConstantes.METADADO.equals(qName)) {
			Metadado metadado = new Metadado(attributes.getValue("descricao"),
					Boolean.parseBoolean(attributes.getValue("contabilizavel")));
			metadado.aplicar(attributes);
			if (selecionado == null) {
				raiz = metadado;
			} else {
				selecionado.add(metadado);
			}
			selecionado = metadado;
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (MetadadoConstantes.METADADO.equals(qName) && selecionado != null) {
			selecionado = selecionado.getPai();
		}
	}

	public Metadado getRaiz() {
		return raiz;
	}
}