package br.com.persist.plugins.arquivo;

import java.text.MessageFormat;
import java.util.ResourceBundle;

public class ArquivoMensagens {
	public static final ResourceBundle bundle = ResourceBundle
			.getBundle(ArquivoMensagens.class.getPackage().getName() + ".mensagens");

	private ArquivoMensagens() {
	}

	public static String getString(String chave, Object... argumentos) {
		if (argumentos == null || argumentos.length == 0) {
			return bundle.getString(chave);
		}
		return MessageFormat.format(bundle.getString(chave), argumentos);
	}
}