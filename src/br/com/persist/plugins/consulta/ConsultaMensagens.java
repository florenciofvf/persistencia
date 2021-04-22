package br.com.persist.plugins.consulta;

import java.text.MessageFormat;
import java.util.ResourceBundle;

public class ConsultaMensagens {
	public static final ResourceBundle bundle = ResourceBundle
			.getBundle(ConsultaMensagens.class.getPackage().getName() + ".mensagens");

	private ConsultaMensagens() {
	}

	public static String getString(String chave, Object... argumentos) {
		if (argumentos == null || argumentos.length == 0) {
			return bundle.getString(chave);
		}
		return MessageFormat.format(bundle.getString(chave), argumentos);
	}
}