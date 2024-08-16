package br.com.persist.plugins.metadado;

public class MetadadoException extends Exception {
	private static final long serialVersionUID = 1L;

	public MetadadoException(String chave, Object... argumentos) {
		super(MetadadoMensagens.getString(chave, argumentos));
	}

	public MetadadoException(String message, Throwable cause) {
		super(message, cause);
	}

	public MetadadoException(String string, boolean ehChave) {
		super(ehChave ? MetadadoMensagens.getString(string) : string);
	}

	public MetadadoException(Throwable cause) {
		super(cause);
	}

	public MetadadoException(String chave) {
		this(chave, true);
	}
}