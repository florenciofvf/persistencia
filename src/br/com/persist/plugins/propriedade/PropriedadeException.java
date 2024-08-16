package br.com.persist.plugins.propriedade;

public class PropriedadeException extends Exception {
	private static final long serialVersionUID = 1L;

	public PropriedadeException(Throwable cause) {
		super(cause);
	}

	public PropriedadeException(String message, Throwable cause) {
		super(message, cause);
	}

	public PropriedadeException(String string, boolean ehChave) {
		super(ehChave ? PropriedadeMensagens.getString(string) : string);
	}

	public PropriedadeException(String chave) {
		this(chave, true);
	}

	public PropriedadeException(String chave, Object... argumentos) {
		super(PropriedadeMensagens.getString(chave, argumentos));
	}
}