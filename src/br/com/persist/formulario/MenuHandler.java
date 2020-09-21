package br.com.persist.formulario;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import br.com.persist.marca.XMLHandler;

class MenuHandler extends XMLHandler {
	private final MenuColetor coletor;
	private MenuApp selecionado;

	MenuHandler(MenuColetor coletor) {
		this.coletor = coletor;
		coletor.init();
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if ("menu".equals(qName)) {
			MenuApp menu = selecionado;
			selecionado = new MenuApp();
			selecionado.aplicar(attributes);
			if (menu == null) {
				coletor.getMenus().add(selecionado);
			} else {
				menu.add(selecionado);
			}
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if ("menu".equals(qName) && selecionado != null) {
			selecionado = selecionado.getPai();
		}
	}
}