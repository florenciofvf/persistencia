package br.com.persist.plugins.biblio;

import java.text.MessageFormat;
import java.util.ResourceBundle;

public class BiblioMensagens {
	public static final ResourceBundle bundle = ResourceBundle
			.getBundle(BiblioMensagens.class.getPackage().getName() + ".mensagens");

	private BiblioMensagens() {
	}

	public static String getString(String chave, Object... argumentos) {
		if (argumentos == null || argumentos.length == 0) {
			return bundle.getString(chave);
		}
		return MessageFormat.format(bundle.getString(chave), argumentos);
	}
}