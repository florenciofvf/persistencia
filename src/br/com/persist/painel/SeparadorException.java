package br.com.persist.painel;

public class SeparadorException extends Exception {
	private static final long serialVersionUID = 1L;

	public SeparadorException(Throwable cause) {
		super(cause);
	}

	public SeparadorException(String message) {
		super(message);
	}

	public SeparadorException() {
		super();
	}
}