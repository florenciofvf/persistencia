package br.com.persist.plugins.propriedade;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import br.com.persist.marca.XMLHandler;

class PropriedadeHandler extends XMLHandler {
	public static final String ATRIBUTO = "atributo";
	public static final String PROPERTY = "property";
	public static final String CONFIG = "config";
	public static final String BLOCO = "bloco";
	public static final String PARAM = "param";
	public static final String VALUE = "value";
	public static final String NAME = "name";
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

	private String getName(Attributes atts) {
		return atts.getValue(NAME);
	}

	private String getValue(Attributes atts) {
		return atts.getValue(VALUE);
	}

	private Container criar(String qName, Attributes atts) {
		if (CONFIG.equals(qName)) {
			return new Config(getName(atts));
		} else if (ATRIBUTO.equals(qName)) {
			return new Atributo(getName(atts), getValue(atts));
		} else if (BLOCO.equals(qName)) {
			return new Bloco(getName(atts));
		} else if (PARAM.equals(qName)) {
			return new Param(getName(atts), getValue(atts));
		} else if (PROPERTY.equals(qName)) {
			return new Propriedade(getName(atts), getValue(atts));
		}
		throw new IllegalStateException();
	}
}