package br.com.persist.plugins.robo;

import java.text.MessageFormat;
import java.util.ResourceBundle;

public class RoboMensagens {
	public static final ResourceBundle bundle = ResourceBundle
			.getBundle(RoboMensagens.class.getPackage().getName() + ".mensagens");

	private RoboMensagens() {
	}

	public static String getString(String chave, Object... argumentos) {
		if (argumentos == null || argumentos.length == 0) {
			return bundle.getString(chave);
		}
		return MessageFormat.format(bundle.getString(chave), argumentos);
	}
}