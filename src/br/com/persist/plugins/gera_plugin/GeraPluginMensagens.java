package br.com.persist.plugins.gera_plugin;

import java.text.MessageFormat;
import java.util.ResourceBundle;

public class GeraPluginMensagens {
	public static final ResourceBundle bundle = ResourceBundle
			.getBundle(GeraPluginMensagens.class.getPackage().getName() + ".mensagens");

	private GeraPluginMensagens() {
	}

	public static String getString(String chave, Object... argumentos) {
		if (argumentos == null || argumentos.length == 0) {
			return bundle.getString(chave);
		}
		return MessageFormat.format(bundle.getString(chave), argumentos);
	}
}