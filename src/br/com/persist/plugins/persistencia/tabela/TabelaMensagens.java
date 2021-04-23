package br.com.persist.plugins.persistencia.tabela;

import java.text.MessageFormat;
import java.util.ResourceBundle;

public class TabelaMensagens {
	public static final ResourceBundle bundle = ResourceBundle
			.getBundle(TabelaMensagens.class.getPackage().getName() + ".mensagens");

	private TabelaMensagens() {
	}

	public static String getString(String chave, Object... argumentos) {
		if (argumentos == null || argumentos.length == 0) {
			return bundle.getString(chave);
		}
		return MessageFormat.format(bundle.getString(chave), argumentos);
	}
}