package br.com.persist.plugins.anotacao;

import java.text.MessageFormat;
import java.util.ResourceBundle;

public class AnotacaoMensagens {
	public static final ResourceBundle bundle = ResourceBundle
			.getBundle(AnotacaoMensagens.class.getPackage().getName() + ".mensagens");

	private AnotacaoMensagens() {
	}

	public static String getString(String chave, Object... argumentos) {
		if (argumentos == null || argumentos.length == 0) {
			return bundle.getString(chave);
		}
		return MessageFormat.format(bundle.getString(chave), argumentos);
	}
}