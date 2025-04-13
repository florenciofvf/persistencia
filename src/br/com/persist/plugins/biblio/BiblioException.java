package br.com.persist.plugins.biblio;

public class BiblioException extends Exception {
	private static final long serialVersionUID = 1L;

	public BiblioException(String chave, Object... argumentos) {
		super(BiblioMensagens.getString(chave, argumentos));
	}

	public BiblioException(String message, Throwable cause) {
		super(message, cause);
	}

	public BiblioException(String string, boolean ehChave) {
		super(ehChave ? BiblioMensagens.getString(string) : string);
	}

	public BiblioException(Throwable cause) {
		super(cause);
	}

	public BiblioException(String chave) {
		this(chave, true);
	}
}