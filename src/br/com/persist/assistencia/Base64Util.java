package br.com.persist.assistencia;

import javax.xml.bind.DatatypeConverter;

public class Base64Util {
	private Base64Util() {
	}

	public static String criarBase64(String string) {
		return DatatypeConverter.printBase64Binary(string.getBytes());
	}
}