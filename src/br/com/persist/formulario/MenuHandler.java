package br.com.persist.formulario;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import br.com.persist.marca.XMLHandler;

class MenuHandler extends XMLHandler {
	private final List<MenuXML> menus;
	private MenuXML selecionado;

	MenuHandler() {
		menus = new ArrayList<>();
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if ("menu".equals(qName)) {
			selecionado = MenuXML.criar(attributes);
			menus.add(selecionado);
		} else if ("menuItem".equals(qName)) {
			MenuItemXML item = MenuItemXML.criar(attributes);
			if (selecionado != null) {
				selecionado.add(item);
			}
		}
	}

	public List<MenuXML> getMenus() {
		return menus;
	}
}