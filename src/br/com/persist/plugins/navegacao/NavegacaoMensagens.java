package br.com.persist.plugins.navegacao;

import java.text.MessageFormat;
import java.util.ResourceBundle;

public class NavegacaoMensagens {
	public static final ResourceBundle bundle = ResourceBundle
			.getBundle(NavegacaoMensagens.class.getPackage().getName() + ".mensagens");

	private NavegacaoMensagens() {
	}

	public static String getString(String chave, Object... argumentos) {
		if (argumentos == null || argumentos.length == 0) {
			return bundle.getString(chave);
		}
		return MessageFormat.format(bundle.getString(chave), argumentos);
	}
}