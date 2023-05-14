package br.com.persist.plugins.quebra_log;

public class QuebraLogException extends Exception {
	private static final long serialVersionUID = 1L;

	public QuebraLogException(Throwable cause) {
		super(cause);
	}

	public QuebraLogException(String chave) {
		super(QuebraLogMensagens.getString(chave));
	}
}