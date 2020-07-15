package br.com.persist.util;

import java.text.MessageFormat;
import java.util.ResourceBundle;

public class Mensagens {
	public static final ResourceBundle bundle = ResourceBundle.getBundle("mensagens");

	private Mensagens() {
	}

	public static String getString(String chave, Object... argumentos) {
		if (argumentos == null || argumentos.length == 0) {
			return bundle.getString(chave);
		}

		return MessageFormat.format(bundle.getString(chave), argumentos);
	}

	public static String getTituloAplicacao() {
		return getString("label.persistencia");
	}
}