package br.com.persist.plugins.sistema;

import java.text.MessageFormat;
import java.util.ResourceBundle;

public class SistemaMensagens {
	public static final ResourceBundle bundle = ResourceBundle
			.getBundle(SistemaMensagens.class.getPackage().getName() + ".mensagens");

	private SistemaMensagens() {
	}

	public static String getString(String chave, Object... argumentos) {
		if (argumentos == null || argumentos.length == 0) {
			return bundle.getString(chave);
		}
		return MessageFormat.format(bundle.getString(chave), argumentos);
	}
}