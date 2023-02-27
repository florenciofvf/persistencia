package br.com.persist.plugins.mapa.config;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import br.com.persist.plugins.mapa.Atributo;
import br.com.persist.plugins.mapa.Container;
import br.com.persist.plugins.mapa.Objeto;

public class Handler extends DefaultHandler {
	Objeto selecionado;
	Objeto raiz;

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (raiz == null) {
			raiz = new Objeto(qName);
			selecionado = raiz;
			for (int i = 0; i < attributes.getLength(); i++) {
				Atributo atributo = new Atributo(attributes.getQName(i), attributes.getValue(i));
				raiz.adicionarAtributo(atributo);
			}
			raiz.criarCorRGB();
			return;
		}
		Objeto objeto = new Objeto(qName);
		for (int i = 0; i < attributes.getLength(); i++) {
			Atributo atributo = new Atributo(attributes.getQName(i), attributes.getValue(i));
			objeto.adicionarAtributo(atributo);
		}
		objeto.criarCorRGB();
		selecionado.adicionarFilho(objeto);
		if (!objeto.isRef()) {
			selecionado = objeto;
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (!Container.isReferencia(qName)) {
			selecionado = (Objeto) selecionado.getPai();
		}
	}
}