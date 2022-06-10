package br.com.persist.plugins.checagem;

public class ChecagemException extends Exception {
	private static final long serialVersionUID = 1L;

	public ChecagemException(Throwable cause) {
		super(cause);
	}

	public ChecagemException(String message) {
		super(message);
	}
}