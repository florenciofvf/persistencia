package br.com.persist.plugins.instrucao;

public class InstrucaoException extends Exception {
	private static final long serialVersionUID = 1L;

	public InstrucaoException(Throwable cause) {
		super(cause);
	}

	public InstrucaoException(String message, Throwable cause) {
		super(message, cause);
	}

	public InstrucaoException(String string, boolean ehChave) {
		super(ehChave ? InstrucaoMensagens.getString(string) : string);
	}

	public InstrucaoException(String chave) {
		this(chave, true);
	}

	public InstrucaoException(String chave, Object... argumentos) {
		super(InstrucaoMensagens.getString(chave, argumentos));
	}
}