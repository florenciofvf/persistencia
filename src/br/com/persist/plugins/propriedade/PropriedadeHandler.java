package br.com.persist.plugins.propriedade;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import br.com.persist.marca.XMLHandler;

class PropriedadeHandler extends XMLHandler {
	private Container selecionado;
	private Arvore raiz;

	@Override
	public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
		if (Arvore.CONFIGURACAO.equals(qName)) {
			raiz = (Arvore) criar(qName, atts);
			selecionado = raiz;
		} else if (raiz != null) {
			Container novo = criar(qName, atts);
			if (novo != null) {
				try {
					selecionado.adicionar(novo);
				} catch (PropriedadeException e) {
					throw new SAXException(e);
				}
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

	public Arvore getRaiz() {
		return raiz;
	}

	private Container criar(String qName, Attributes atts) {
		if (Arvore.CONFIGURACAO.equals(qName)) {
			return Arvore.criar(atts);
		} else if (Config.TAG_CONFIG.equals(qName)) {
			return Config.criar(atts);
		} else if (Campo.TAB_CAMPO.equals(qName)) {
			return Campo.criar(atts);
		} else if (Modulo.TAG_MODULO.equals(qName)) {
			return Modulo.criar(atts);
		} else if (Map.TAG_MAP.equals(qName)) {
			return Map.criar(atts);
		} else if (Property.TAG_PROPERTY.equals(qName)) {
			return Property.criar(atts);
		}
		return null;
	}
}