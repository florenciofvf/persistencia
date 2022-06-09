package br.com.persist.plugins.checagem;

import java.text.MessageFormat;
import java.util.ResourceBundle;

public class ChecagemMensagens {
	public static final ResourceBundle bundle = ResourceBundle
			.getBundle(ChecagemMensagens.class.getPackage().getName() + ".mensagens");

	private ChecagemMensagens() {
	}

	public static String getString(String chave, Object... argumentos) {
		if (argumentos == null || argumentos.length == 0) {
			return bundle.getString(chave);
		}
		return MessageFormat.format(bundle.getString(chave), argumentos);
	}
}