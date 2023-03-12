package br.com.persist.plugins.mapa;

import java.io.File;

import br.com.persist.marca.XML;
import br.com.persist.marca.XMLException;

public class MontaObjeto {
	private MontaObjeto() {
	}

	public static MapaHandler montarObjeto(String arquivo) throws XMLException {
		return montarObjeto(new File(arquivo));
	}

	public static MapaHandler montarObjeto(File file) throws XMLException {
		if (!file.exists()) {
			return null;
		}
		return montar(file);
	}

	private static MapaHandler montar(File file) throws XMLException {
		try {
			MapaHandler handler = new MapaHandler();
			XML.processar(file, handler);
			return handler;
		} catch (Exception e) {
			throw new XMLException(e);
		}
	}
}