package br.com.persist.plugins.execucao;

public class ExecucaoException extends Exception {
	private static final long serialVersionUID = 1L;

	public ExecucaoException(Throwable cause) {
		super(cause);
	}

	public ExecucaoException(String message, Throwable cause) {
		super(message, cause);
	}

	public ExecucaoException(String string, boolean ehChave) {
		super(ehChave ? ExecucaoMensagens.getString(string) : string);
	}

	public ExecucaoException(String chave) {
		this(chave, true);
	}

	public ExecucaoException(String chave, Object... argumentos) {
		super(ExecucaoMensagens.getString(chave, argumentos));
	}
}