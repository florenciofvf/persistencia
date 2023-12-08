package br.com.persist.plugins.atributo;

import java.text.MessageFormat;
import java.util.ResourceBundle;

public class AtributoMensagens {
	public static final ResourceBundle bundle = ResourceBundle
			.getBundle(AtributoMensagens.class.getPackage().getName() + ".mensagens");

	private AtributoMensagens() {
	}

	public static String getString(String chave, Object... argumentos) {
		if (argumentos == null || argumentos.length == 0) {
			return bundle.getString(chave);
		}
		return MessageFormat.format(bundle.getString(chave), argumentos);
	}
}