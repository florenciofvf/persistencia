package br.com.persist.plugins.ambiente;

import java.text.MessageFormat;
import java.util.ResourceBundle;

public class AmbienteMensagens {
	public static final ResourceBundle bundle = ResourceBundle
			.getBundle(AmbienteMensagens.class.getPackage().getName() + ".mensagens");

	private AmbienteMensagens() {
	}

	public static String getString(String chave, Object... argumentos) {
		if (argumentos == null || argumentos.length == 0) {
			return bundle.getString(chave);
		}
		return MessageFormat.format(bundle.getString(chave), argumentos);
	}
}