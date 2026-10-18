package br.com.persist.plugins.labirinto;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import br.com.persist.marca.XMLHandler;

class LabirintoXMLHandler extends XMLHandler {
	Matriz matriz;

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if ("matriz".equals(qName)) {
			int linhas = Integer.parseInt(attributes.getValue("linhas"));
			int colunas = Integer.parseInt(attributes.getValue("colunas"));
			boolean auto = "true".equals(attributes.getValue("auto"));
			matriz = new Matriz(linhas, colunas);
			if (auto) {
				matriz.preencher();
			}
		} else if ("cell".equals(qName)) {
			boolean vazio = "true".equals(attributes.getValue("vazio"));
			boolean pivo = "true".equals(attributes.getValue("pivo"));

			Bloco bloco = new Bloco(vazio);

			if (vazio && pivo) {
				Pivo objPivo = new Pivo();
				objPivo.ocupar(bloco);
			}

			if (matriz == null) {
				throw new SAXException("Defina o elemento matriz");
			}

			matriz.add(bloco);
		}
	}
}