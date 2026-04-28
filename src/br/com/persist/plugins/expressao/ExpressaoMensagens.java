package br.com.persist.plugins.expressao;

import java.text.MessageFormat;
import java.util.ResourceBundle;

public class ExpressaoMensagens {
	public static final ResourceBundle bundle = ResourceBundle
			.getBundle(ExpressaoMensagens.class.getPackage().getName() + ".mensagens");

	private ExpressaoMensagens() {
	}

	public static String getString(String chave, Object... argumentos) {
		if (argumentos == null || argumentos.length == 0) {
			return bundle.getString(chave);
		}
		return MessageFormat.format(bundle.getString(chave), argumentos);
	}
}