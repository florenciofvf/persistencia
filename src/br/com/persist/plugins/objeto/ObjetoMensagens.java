package br.com.persist.plugins.objeto;

import java.text.MessageFormat;
import java.util.ResourceBundle;

import br.com.persist.assistencia.Util;

public class ObjetoMensagens {
	public static final ResourceBundle bundle = ResourceBundle
			.getBundle(ObjetoMensagens.class.getPackage().getName() + ".mensagens");

	private ObjetoMensagens() {
	}

	public static String getString(String chave, Object... argumentos) {
		if (argumentos == null || argumentos.length == 0) {
			return bundle.getString(chave);
		}
		return MessageFormat.format(bundle.getString(chave), argumentos);
	}

	public static String getStringHtml(String chave) {
		return Util.getHtml(getString(chave));
	}
}