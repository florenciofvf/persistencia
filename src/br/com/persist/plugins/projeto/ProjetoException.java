package br.com.persist.plugins.projeto;

public class ProjetoException extends Exception {
	private static final long serialVersionUID = 1L;

	public ProjetoException(String chave, Object... argumentos) {
		super(ProjetoMensagens.getString(chave, argumentos));
	}

	public ProjetoException(String message, Throwable cause) {
		super(message, cause);
	}

	public ProjetoException(String string, boolean ehChave) {
		super(ehChave ? ProjetoMensagens.getString(string) : string);
	}

	public ProjetoException(Throwable cause) {
		super(cause);
	}

	public ProjetoException(String chave) {
		this(chave, true);
	}
}