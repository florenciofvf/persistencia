package br.com.persist.plugins.ouvinte;

import java.text.MessageFormat;
import java.util.ResourceBundle;

public class OuvinteMensagens {
	public static final ResourceBundle bundle = ResourceBundle
			.getBundle(OuvinteMensagens.class.getPackage().getName() + ".mensagens");

	private OuvinteMensagens() {
	}

	public static String getString(String chave, Object... argumentos) {
		if (argumentos == null || argumentos.length == 0) {
			return bundle.getString(chave);
		}
		return MessageFormat.format(bundle.getString(chave), argumentos);
	}
}