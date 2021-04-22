package br.com.persist.plugins.update;

import java.text.MessageFormat;
import java.util.ResourceBundle;

public class UpdateMensagens {
	public static final ResourceBundle bundle = ResourceBundle
			.getBundle(UpdateMensagens.class.getPackage().getName() + ".mensagens");

	private UpdateMensagens() {
	}

	public static String getString(String chave, Object... argumentos) {
		if (argumentos == null || argumentos.length == 0) {
			return bundle.getString(chave);
		}
		return MessageFormat.format(bundle.getString(chave), argumentos);
	}
}