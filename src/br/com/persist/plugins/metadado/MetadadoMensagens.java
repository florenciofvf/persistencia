package br.com.persist.plugins.metadado;

import java.text.MessageFormat;
import java.util.ResourceBundle;

public class MetadadoMensagens {
	public static final ResourceBundle bundle = ResourceBundle
			.getBundle(MetadadoMensagens.class.getPackage().getName() + ".mensagens");

	private MetadadoMensagens() {
	}

	public static String getString(String chave, Object... argumentos) {
		if (argumentos == null || argumentos.length == 0) {
			return bundle.getString(chave);
		}
		return MessageFormat.format(bundle.getString(chave), argumentos);
	}
}