package br.com.persist.plugins.atributo;

public class AtributoException extends Exception {
	private static final long serialVersionUID = 1L;

	public AtributoException(Throwable cause) {
		super(cause);
	}

	public AtributoException(String message, Throwable cause) {
		super(message, cause);
	}

	public AtributoException(String string, boolean ehChave) {
		super(ehChave ? AtributoMensagens.getString(string) : string);
	}

	public AtributoException(String chave) {
		this(chave, true);
	}

	public AtributoException(String chave, Object... argumentos) {
		super(AtributoMensagens.getString(chave, argumentos));
	}
}