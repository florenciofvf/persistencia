package br.com.persist.plugins.variaveis;

import java.text.MessageFormat;
import java.util.ResourceBundle;

public class VariavelMensagens {
	public static final ResourceBundle bundle = ResourceBundle
			.getBundle(VariavelMensagens.class.getPackage().getName() + ".mensagens");

	private VariavelMensagens() {
	}

	public static String getString(String chave, Object... argumentos) {
		if (argumentos == null || argumentos.length == 0) {
			return bundle.getString(chave);
		}
		return MessageFormat.format(bundle.getString(chave), argumentos);
	}
}