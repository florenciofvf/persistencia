package br.com.persist.plugins.objeto.alter;

import java.text.MessageFormat;
import java.util.ResourceBundle;

public class AlternativoMensagens {
	public static final ResourceBundle bundle = ResourceBundle
			.getBundle(AlternativoMensagens.class.getPackage().getName() + ".mensagens");

	private AlternativoMensagens() {
	}

	public static String getString(String chave, Object... argumentos) {
		if (argumentos == null || argumentos.length == 0) {
			return bundle.getString(chave);
		}
		return MessageFormat.format(bundle.getString(chave), argumentos);
	}
}