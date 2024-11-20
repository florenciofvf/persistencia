package br.com.persist.plugins.projeto;

import java.text.MessageFormat;
import java.util.ResourceBundle;

public class ProjetoMensagens {
	public static final ResourceBundle bundle = ResourceBundle
			.getBundle(ProjetoMensagens.class.getPackage().getName() + ".mensagens");

	private ProjetoMensagens() {
	}

	public static String getString(String chave, Object... argumentos) {
		if (argumentos == null || argumentos.length == 0) {
			return bundle.getString(chave);
		}
		return MessageFormat.format(bundle.getString(chave), argumentos);
	}
}