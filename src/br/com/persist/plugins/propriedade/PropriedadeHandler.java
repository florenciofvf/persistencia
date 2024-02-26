package br.com.persist.plugins.propriedade;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import br.com.persist.marca.XMLHandler;

class PropriedadeHandler extends XMLHandler {
	private Container selecionado;
	private Raiz raiz;

	@Override
	public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
		if (raiz == null) {
			raiz = new Raiz();
			selecionado = raiz;
		} else {
			Container novo = criar(qName, atts);
			selecionado.adicionar(novo);
			selecionado = novo;
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		selecionado = selecionado.pai;
	}

	public Raiz getRaiz() {
		return raiz;
	}

	private String get(Attributes atts, String chave) {
		return atts.getValue(chave);
	}

	private Container criar(String qName, Attributes atts) {
		if ("objeto".equals(qName)) {
			return new Objeto(get(atts, "id"));
		} else if ("campo".equals(qName)) {
			return new Campo(get(atts, "nome"), get(atts, "valor"));
		} else if ("bloco".equals(qName)) {
			return new Bloco(get(atts, "nome"));
		} else if ("map".equals(qName)) {
			return new Map(get(atts, "chave"), get(atts, "idObjeto"));
		} else if ("property".equals(qName)) {
			return new Propriedade(get(atts, "name"), get(atts, "value"));
		}
		throw new IllegalStateException();
	}
}