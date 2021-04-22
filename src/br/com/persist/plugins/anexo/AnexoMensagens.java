package br.com.persist.plugins.anexo;

import java.text.MessageFormat;
import java.util.ResourceBundle;

public class AnexoMensagens {
	public static final ResourceBundle bundle = ResourceBundle
			.getBundle(AnexoMensagens.class.getPackage().getName() + ".mensagens");

	private AnexoMensagens() {
	}

	public static String getString(String chave, Object... argumentos) {
		if (argumentos == null || argumentos.length == 0) {
			return bundle.getString(chave);
		}
		return MessageFormat.format(bundle.getString(chave), argumentos);
	}
}