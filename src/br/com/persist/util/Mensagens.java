package br.com.persist.util;

import java.util.ResourceBundle;

public class Mensagens {
	public static final ResourceBundle bundle = ResourceBundle.getBundle("mensagens");

	private Mensagens() {
	}

	public static String getString(String chave) {
		return bundle.getString(chave);
	}
}