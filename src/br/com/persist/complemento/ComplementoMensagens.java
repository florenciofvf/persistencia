package br.com.persist.complemento;

import java.text.MessageFormat;
import java.util.ResourceBundle;

public class ComplementoMensagens {
	public static final ResourceBundle bundle = ResourceBundle
			.getBundle(ComplementoMensagens.class.getPackage().getName() + ".mensagens");

	private ComplementoMensagens() {
	}

	public static String getString(String chave, Object... argumentos) {
		if (argumentos == null || argumentos.length == 0) {
			return bundle.getString(chave);
		}
		return MessageFormat.format(bundle.getString(chave), argumentos);
	}
}