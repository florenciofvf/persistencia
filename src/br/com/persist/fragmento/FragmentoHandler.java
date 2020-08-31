package br.com.persist.fragmento;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import br.com.persist.xml.XMLHandler;

class FragmentoHandler extends XMLHandler {
	private final FragmentoColetor coletor;

	FragmentoHandler(FragmentoColetor coletor) {
		this.coletor = coletor;
		coletor.init();
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if ("fragmento".equals(qName)) {
			Fragmento f = new Fragmento();
			f.aplicar(attributes);
			coletor.getFragmentos().add(f);
		}
	}
}