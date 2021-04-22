package br.com.persist.plugins.fragmento;

import java.text.MessageFormat;
import java.util.ResourceBundle;

public class FragmentoMensagens {
	public static final ResourceBundle bundle = ResourceBundle
			.getBundle(FragmentoMensagens.class.getPackage().getName() + ".mensagens");

	private FragmentoMensagens() {
	}

	public static String getString(String chave, Object... argumentos) {
		if (argumentos == null || argumentos.length == 0) {
			return bundle.getString(chave);
		}
		return MessageFormat.format(bundle.getString(chave), argumentos);
	}
}