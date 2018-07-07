package br.com.persist.xml;

import java.io.File;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import br.com.persist.Objeto;
import br.com.persist.Relacao;

public class XML {

	public static void processar(File file, List<Objeto> objetos, List<Relacao> relacoes) throws Exception {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		factory.setNamespaceAware(true);
		factory.setXIncludeAware(true);

		SAXParser parser = factory.newSAXParser();
		XMLHandler handler = new XMLHandler(objetos, relacoes);
		parser.parse(file, handler);
	}

}

class XMLHandler extends DefaultHandler {
	final List<Relacao> relacoes;
	final List<Objeto> objetos;

	public XMLHandler(List<Objeto> objetos, List<Relacao> relacoes) {
		this.relacoes = relacoes;
		this.objetos = objetos;
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if ("objeto".equals(qName)) {
			Objeto objeto = new Objeto();
			objeto.aplicar(attributes);
			objetos.add(objeto);
		} else if ("relacao".equals(qName)) {
			Objeto objeto1 = getObjeto(attributes.getValue("objeto1"));
			Objeto objeto2 = getObjeto(attributes.getValue("objeto2"));

			boolean ponto1 = Boolean.parseBoolean(attributes.getValue("ponto1"));
			boolean ponto2 = Boolean.parseBoolean(attributes.getValue("ponto2"));

			Relacao relacao = new Relacao(objeto1, ponto1, objeto2, ponto2);
			relacao.aplicar(attributes);
			relacoes.add(relacao);
		}
	}

	private Objeto getObjeto(String nome) {
		for (Objeto objeto : objetos) {
			if (nome.equals(objeto.getId())) {
				return objeto;
			}
		}

		throw new IllegalStateException();
	}
}