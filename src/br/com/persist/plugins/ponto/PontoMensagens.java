package br.com.persist.plugins.ponto;

import java.text.MessageFormat;
import java.util.ResourceBundle;

public class PontoMensagens {
	public static final ResourceBundle bundle = ResourceBundle
			.getBundle(PontoMensagens.class.getPackage().getName() + ".mensagens");

	private PontoMensagens() {
	}

	public static String getString(String chave, Object... argumentos) {
		if (argumentos == null || argumentos.length == 0) {
			return bundle.getString(chave);
		}
		return MessageFormat.format(bundle.getString(chave), argumentos);
	}
}