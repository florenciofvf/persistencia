package br.com.persist.plugins.conexao;

import java.text.MessageFormat;
import java.util.ResourceBundle;

public class ConexaoMensagens {
	public static final ResourceBundle bundle = ResourceBundle
			.getBundle(ConexaoMensagens.class.getPackage().getName() + ".mensagens");

	private ConexaoMensagens() {
	}

	public static String getString(String chave, Object... argumentos) {
		if (argumentos == null || argumentos.length == 0) {
			return bundle.getString(chave);
		}
		return MessageFormat.format(bundle.getString(chave), argumentos);
	}
}