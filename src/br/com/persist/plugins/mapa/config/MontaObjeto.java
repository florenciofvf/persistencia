package br.com.persist.plugins.mapa.config;

import java.io.File;

import javax.xml.XMLConstants;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import br.com.persist.marca.XMLException;
import br.com.persist.plugins.mapa.Objeto;

public class MontaObjeto {
	private MontaObjeto() {
	}

	public static Objeto montarObjeto(String arquivo) throws XMLException {
		return montarObjeto(new File(arquivo));
	}

	public static Objeto montarObjeto(File file) throws XMLException {
		if (!file.exists()) {
			return null;
		}
		return montar(file);
	}

	private static Objeto montar(File file) throws XMLException {
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
			factory.setNamespaceAware(true);
			factory.setXIncludeAware(true);
			SAXParser parser = factory.newSAXParser();
			Handler handler = new Handler();
			parser.parse(file, handler);
			return handler.raiz;
		} catch (Exception e) {
			throw new XMLException(e);
		}
	}
}