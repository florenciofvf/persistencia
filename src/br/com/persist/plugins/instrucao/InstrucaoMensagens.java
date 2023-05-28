package br.com.persist.plugins.instrucao;

import java.text.MessageFormat;
import java.util.ResourceBundle;

public class InstrucaoMensagens {
	public static final ResourceBundle bundle = ResourceBundle
			.getBundle(InstrucaoMensagens.class.getPackage().getName() + ".mensagens");

	private InstrucaoMensagens() {
	}

	public static String getString(String chave, Object... argumentos) {
		if (argumentos == null || argumentos.length == 0) {
			return bundle.getString(chave);
		}
		return MessageFormat.format(bundle.getString(chave), argumentos);
	}
}