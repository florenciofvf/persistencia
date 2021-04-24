package br.com.persist.plugins.persistencia;

import java.text.MessageFormat;
import java.util.ResourceBundle;

public class PersistenciaMensagens {
	public static final ResourceBundle bundle = ResourceBundle
			.getBundle(PersistenciaMensagens.class.getPackage().getName() + ".mensagens");

	private PersistenciaMensagens() {
	}

	public static String getString(String chave, Object... argumentos) {
		if (argumentos == null || argumentos.length == 0) {
			return bundle.getString(chave);
		}
		return MessageFormat.format(bundle.getString(chave), argumentos);
	}
}