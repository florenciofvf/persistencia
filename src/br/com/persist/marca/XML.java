package br.com.persist.marca;

import java.io.File;

import javax.xml.XMLConstants;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class XML {
	private XML() {
	}

	private static SAXParserFactory criarSAXParserFactory() throws XMLException {
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
			factory.setNamespaceAware(true);
			factory.setXIncludeAware(true);
			return factory;
		} catch (Exception e) {
			throw new XMLException(e);
		}
	}

	public static void processar(File file, XMLHandler handler) throws XMLException {
		if (file != null) {
			if (file.exists() && file.isFile()) {
				processarFile(file, handler);
			} else {
				throw new XMLException("Inexistente >>> " + file.getAbsolutePath());
			}
		} else {
			throw new XMLException("File nulo.");
		}
	}

	private static void processarFile(File file, XMLHandler handler) throws XMLException {
		try {
			SAXParserFactory factory = criarSAXParserFactory();
			SAXParser parser = factory.newSAXParser();
			parser.parse(file, handler);
		} catch (Exception e) {
			throw new XMLException(e);
		}
	}
}