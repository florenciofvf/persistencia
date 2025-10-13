package br.com.persist.plugins.sistema;

public class SistemaException extends Exception {
	private static final long serialVersionUID = 1L;

	public SistemaException(String chave, Object... argumentos) {
		super(SistemaMensagens.getString(chave, argumentos));
	}

	public SistemaException(String message, Throwable cause) {
		super(message, cause);
	}

	public SistemaException(String string, boolean ehChave) {
		super(ehChave ? SistemaMensagens.getString(string) : string);
	}

	public SistemaException(Throwable cause) {
		super(cause);
	}

	public SistemaException(String chave) {
		this(chave, true);
	}
}