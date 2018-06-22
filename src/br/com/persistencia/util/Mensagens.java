package br.com.persistencia.util;

import java.util.ResourceBundle;

public class Mensagens {
	public static ResourceBundle bundle = ResourceBundle.getBundle("mensagens");

	private Mensagens() {
	}

	public static String getString(String chave) {
		return bundle.getString(chave);
	}
//
//	public static boolean getStringBoolean(String chave) {
//		return Boolean.parseBoolean(getString(chave));
//	}
}