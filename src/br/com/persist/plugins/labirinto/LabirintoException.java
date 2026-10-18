package br.com.persist.plugins.labirinto;

public class LabirintoException extends Exception {
	private static final long serialVersionUID = 1L;

	public LabirintoException(String chave, Object... argumentos) {
		super(LabirintoMensagens.getString(chave, argumentos));
	}

	public LabirintoException(String message, Throwable cause) {
		super(message, cause);
	}

	public LabirintoException(String string, boolean ehChave) {
		super(ehChave ? LabirintoMensagens.getString(string) : string);
	}

	public LabirintoException(Throwable cause) {
		super(cause);
	}

	public LabirintoException(String chave) {
		this(chave, true);
	}
}