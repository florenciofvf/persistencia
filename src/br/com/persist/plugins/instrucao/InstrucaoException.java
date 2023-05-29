package br.com.persist.plugins.instrucao;

public class InstrucaoException extends Exception {
	private static final long serialVersionUID = 1L;

	public InstrucaoException(Throwable cause) {
		super(cause);
	}

	public InstrucaoException(String chave) {
		super(InstrucaoMensagens.getString(chave));
	}

	public InstrucaoException(String chave, Object... argumentos) {
		super(InstrucaoMensagens.getString(chave, argumentos));
	}
}