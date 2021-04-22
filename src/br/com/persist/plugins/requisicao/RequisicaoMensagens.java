package br.com.persist.plugins.requisicao;

import java.text.MessageFormat;
import java.util.ResourceBundle;

public class RequisicaoMensagens {
	public static final ResourceBundle bundle = ResourceBundle
			.getBundle(RequisicaoMensagens.class.getPackage().getName() + ".mensagens");

	private RequisicaoMensagens() {
	}

	public static String getString(String chave, Object... argumentos) {
		if (argumentos == null || argumentos.length == 0) {
			return bundle.getString(chave);
		}
		return MessageFormat.format(bundle.getString(chave), argumentos);
	}
}