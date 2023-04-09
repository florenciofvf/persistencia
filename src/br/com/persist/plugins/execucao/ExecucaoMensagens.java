package br.com.persist.plugins.execucao;

import java.text.MessageFormat;
import java.util.ResourceBundle;

public class ExecucaoMensagens {
	public static final ResourceBundle bundle = ResourceBundle
			.getBundle(ExecucaoMensagens.class.getPackage().getName() + ".mensagens");

	private ExecucaoMensagens() {
	}

	public static String getString(String chave, Object... argumentos) {
		if (argumentos == null || argumentos.length == 0) {
			return bundle.getString(chave);
		}
		return MessageFormat.format(bundle.getString(chave), argumentos);
	}
}