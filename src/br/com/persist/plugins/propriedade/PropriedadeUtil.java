package br.com.persist.plugins.propriedade;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;

import br.com.persist.marca.XML;
import br.com.persist.marca.XMLException;
import br.com.persist.marca.XMLUtil;

public class PropriedadeUtil {
	private PropriedadeUtil() {
	}

	public static Raiz criarRaiz(String string) throws XMLException {
		PropriedadeHandler handler = new PropriedadeHandler();
		XML.processar(new ByteArrayInputStream(string.getBytes()), handler);
		return handler.getRaiz();
	}

	public static String getString(Raiz raiz) throws XMLException {
		StringWriter sw = new StringWriter();
		XMLUtil util = new XMLUtil(sw);
		raiz.salvar(null, util);
		util.close();
		return sw.toString();
	}
}