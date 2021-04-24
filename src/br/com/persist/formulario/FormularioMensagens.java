package br.com.persist.formulario;

import java.text.MessageFormat;
import java.util.ResourceBundle;

public class FormularioMensagens {
	public static final ResourceBundle bundle = ResourceBundle
			.getBundle(FormularioMensagens.class.getPackage().getName() + ".mensagens");

	private FormularioMensagens() {
	}

	public static String getString(String chave, Object... argumentos) {
		if (argumentos == null || argumentos.length == 0) {
			return bundle.getString(chave);
		}
		return MessageFormat.format(bundle.getString(chave), argumentos);
	}
}