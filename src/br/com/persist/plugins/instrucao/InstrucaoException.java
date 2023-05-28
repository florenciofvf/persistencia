package br.com.persist.plugins.instrucao;

public class InstrucaoException extends Exception {
	private static final long serialVersionUID = 1L;

	public InstrucaoException(Throwable cause) {
		super(cause);
	}

	public InstrucaoException(String chave) {
		super(InstrucaoMensagens.getString(chave));
	}
}
