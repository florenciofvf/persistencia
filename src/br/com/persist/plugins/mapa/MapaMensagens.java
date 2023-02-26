package br.com.persist.plugins.mapa;

import java.text.MessageFormat;
import java.util.ResourceBundle;

public class MapaMensagens {
	public static final ResourceBundle bundle = ResourceBundle
			.getBundle(MapaMensagens.class.getPackage().getName() + ".mensagens");

	private MapaMensagens() {
	}

	public static String getString(String chave, Object... argumentos) {
		if (argumentos == null || argumentos.length == 0) {
			return bundle.getString(chave);
		}
		return MessageFormat.format(bundle.getString(chave), argumentos);
	}
}