package br.com.persist.plugins.propriedade;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import br.com.persist.marca.XMLHandler;

class PropriedadeHandler extends XMLHandler {
	private Container selecionado;
	private Raiz raiz;

	@Override
	public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
		if ("raizConfiguracao".equals(qName)) {
			raiz = new Raiz();
			selecionado = raiz;
		} else if (raiz != null) {
			Container novo = criar(qName, atts);
			if (novo != null) {
				selecionado.adicionar(novo);
				selecionado = novo;
			}
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (selecionado != null) {
			selecionado = selecionado.pai;
		}
	}

	public Raiz getRaiz() {
		return raiz;
	}

	private String get(Attributes atts, String chave) {
		return atts.getValue(chave);
	}

	private Container criar(String qName, Attributes atts) {
		if ("config".equals(qName)) {
			return new Config(get(atts, "id"));
		} else if ("campo".equals(qName)) {
			return new Campo(get(atts, "nome"), get(atts, "valor"));
		} else if ("modulo".equals(qName)) {
			Modulo modulo = new Modulo(get(atts, "nome"));
			String string = get(atts, "invalido");
			modulo.setInvalido("true".equalsIgnoreCase(string));
			return modulo;
		} else if ("map".equals(qName)) {
			return new Map(get(atts, "chave"), get(atts, "idConfig"));
		} else if ("property".equals(qName)) {
			return new Propriedade(get(atts, "name"), get(atts, "value"));
		}
		return null;
	}
}