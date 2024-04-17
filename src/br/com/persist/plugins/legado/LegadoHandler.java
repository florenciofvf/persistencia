package br.com.persist.plugins.legado;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import br.com.persist.marca.XMLHandler;

class LegadoHandler extends XMLHandler {
	private List<Legado> lista = new ArrayList<>();

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attr) throws SAXException {
		if (LegadoConstantes.ID.equals(qName)) {
			Legado obj = new LegadoId();
			obj.aplicar(attr);
			lista.add(obj);
		} else if (LegadoConstantes.PROPERTY.equals(qName)) {
			Legado obj = new LegadoProperty();
			obj.aplicar(attr);
			lista.add(obj);
		} else if (LegadoConstantes.KEY_PROPERTY.equals(qName)) {
			Legado obj = new LegadoKeyProperty();
			obj.aplicar(attr);
			lista.add(obj);
		}
	}

	public List<Legado> getLista() {
		return lista;
	}
}