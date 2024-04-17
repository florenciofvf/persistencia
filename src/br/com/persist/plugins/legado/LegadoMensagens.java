package br.com.persist.plugins.legado;

import java.text.MessageFormat;
import java.util.ResourceBundle;

public class LegadoMensagens {
	public static final ResourceBundle bundle = ResourceBundle
			.getBundle(LegadoMensagens.class.getPackage().getName() + ".mensagens");

	private LegadoMensagens() {
	}

	public static String getString(String chave, Object... argumentos) {
		if (argumentos == null || argumentos.length == 0) {
			return bundle.getString(chave);
		}
		return MessageFormat.format(bundle.getString(chave), argumentos);
	}
}