package br.com.persist.plugins.propriedade;

import java.text.MessageFormat;
import java.util.ResourceBundle;

public class PropriedadeMensagens {
	public static final ResourceBundle bundle = ResourceBundle
			.getBundle(PropriedadeMensagens.class.getPackage().getName() + ".mensagens");

	private PropriedadeMensagens() {
	}

	public static String getString(String chave, Object... argumentos) {
		if (argumentos == null || argumentos.length == 0) {
			return bundle.getString(chave);
		}
		return MessageFormat.format(bundle.getString(chave), argumentos);
	}
}