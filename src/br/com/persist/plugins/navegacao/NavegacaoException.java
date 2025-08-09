package br.com.persist.plugins.navegacao;

public class NavegacaoException extends Exception {
	private static final long serialVersionUID = 1L;

	public NavegacaoException(String chave, Object... argumentos) {
		super(NavegacaoMensagens.getString(chave, argumentos));
	}

	public NavegacaoException(String message, Throwable cause) {
		super(message, cause);
	}

	public NavegacaoException(String string, boolean ehChave) {
		super(ehChave ? NavegacaoMensagens.getString(string) : string);
	}

	public NavegacaoException(Throwable cause) {
		super(cause);
	}

	public NavegacaoException(String chave) {
		this(chave, true);
	}
}