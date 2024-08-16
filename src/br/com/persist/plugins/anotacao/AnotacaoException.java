package br.com.persist.plugins.anotacao;

public class AnotacaoException extends Exception {
	private static final long serialVersionUID = 1L;

	public AnotacaoException(Throwable cause) {
		super(cause);
	}

	public AnotacaoException(String message, Throwable cause) {
		super(message, cause);
	}

	public AnotacaoException(String string, boolean ehChave) {
		super(ehChave ? AnotacaoMensagens.getString(string) : string);
	}

	public AnotacaoException(String chave) {
		this(chave, true);
	}

	public AnotacaoException(String chave, Object... argumentos) {
		super(AnotacaoMensagens.getString(chave, argumentos));
	}
}