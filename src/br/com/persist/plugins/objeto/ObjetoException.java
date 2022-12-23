package br.com.persist.plugins.objeto;

public class ObjetoException extends Exception {
	private static final long serialVersionUID = 1L;

	public ObjetoException(Throwable cause) {
		super(cause);
	}

	public ObjetoException(String message) {
		super(message);
	}
}