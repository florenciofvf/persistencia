package br.com.persist.plugins.mapeamento;

import java.text.MessageFormat;
import java.util.ResourceBundle;

public class MapeamentoMensagens {
	public static final ResourceBundle bundle = ResourceBundle
			.getBundle(MapeamentoMensagens.class.getPackage().getName() + ".mensagens");

	private MapeamentoMensagens() {
	}

	public static String getString(String chave, Object... argumentos) {
		if (argumentos == null || argumentos.length == 0) {
			return bundle.getString(chave);
		}
		return MessageFormat.format(bundle.getString(chave), argumentos);
	}
}