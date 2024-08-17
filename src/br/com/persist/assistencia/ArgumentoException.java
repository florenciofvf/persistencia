package br.com.persist.assistencia;

public class ArgumentoException extends Exception {
	private static final long serialVersionUID = 1L;

	public ArgumentoException(Throwable cause) {
		super(cause);
	}

	public ArgumentoException(String message) {
		super(message);
	}
}