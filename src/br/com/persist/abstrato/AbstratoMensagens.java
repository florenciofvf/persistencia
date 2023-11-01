package br.com.persist.abstrato;

import java.text.MessageFormat;
import java.util.ResourceBundle;

public abstract class AbstratoMensagens {
	public static final ResourceBundle bundle = ResourceBundle
			.getBundle(AbstratoMensagens.class.getPackage().getName() + ".mensagens");

	private AbstratoMensagens() {
	}

	public static String getString(String chave, Object... argumentos) {
		if (argumentos == null || argumentos.length == 0) {
			return bundle.getString(chave);
		}
		return MessageFormat.format(bundle.getString(chave), argumentos);
	}
}