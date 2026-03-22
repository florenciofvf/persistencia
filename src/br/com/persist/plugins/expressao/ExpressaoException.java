package br.com.persist.plugins.expressao;

public class ExpressaoException extends Exception {
	private static final long serialVersionUID = 1L;

	public ExpressaoException(String chave, Object... argumentos) {
		super(ExpressaoMensagens.getString(chave, argumentos));
	}

	public ExpressaoException(String message, Throwable cause) {
		super(message, cause);
	}

	public ExpressaoException(String string, boolean ehChave) {
		super(ehChave ? ExpressaoMensagens.getString(string) : string);
	}

	public ExpressaoException(Throwable cause) {
		super(cause);
	}

	public ExpressaoException(String chave) {
		this(chave, true);
	}
}
