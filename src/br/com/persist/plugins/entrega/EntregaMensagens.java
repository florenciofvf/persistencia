package br.com.persist.plugins.entrega;

import java.text.MessageFormat;
import java.util.ResourceBundle;

public class EntregaMensagens {
	public static final ResourceBundle bundle = ResourceBundle
			.getBundle(EntregaMensagens.class.getPackage().getName() + ".mensagens");

	private EntregaMensagens() {
	}

	public static String getString(String chave, Object... argumentos) {
		if (argumentos == null || argumentos.length == 0) {
			return bundle.getString(chave);
		}
		return MessageFormat.format(bundle.getString(chave), argumentos);
	}
}