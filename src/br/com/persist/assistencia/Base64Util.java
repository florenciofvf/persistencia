package br.com.persist.assistencia;

import java.util.Base64;

public class Base64Util {
	private Base64Util() {
	}

	public static String criarBase64(String string) {
		return Base64.getEncoder().encodeToString(string.getBytes());
	}

	public static String criarNomeArquivo(String string) {
		return Base64.getUrlEncoder().encodeToString(string.getBytes());
	}
}