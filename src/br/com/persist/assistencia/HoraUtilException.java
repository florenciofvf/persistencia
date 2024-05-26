package br.com.persist.assistencia;

public class HoraUtilException extends Exception {
	private static final long serialVersionUID = 1L;

	public HoraUtilException(Throwable cause) {
		super(cause);
	}

	public HoraUtilException(String message) {
		super(message);
	}
}