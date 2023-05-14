package br.com.persist.plugins.quebra_log;

import java.text.MessageFormat;
import java.util.ResourceBundle;

public class QuebraLogMensagens {
	public static final ResourceBundle bundle = ResourceBundle
			.getBundle(QuebraLogMensagens.class.getPackage().getName() + ".mensagens");

	private QuebraLogMensagens() {
	}

	public static String getString(String chave, Object... argumentos) {
		if (argumentos == null || argumentos.length == 0) {
			return bundle.getString(chave);
		}
		return MessageFormat.format(bundle.getString(chave), argumentos);
	}
}
