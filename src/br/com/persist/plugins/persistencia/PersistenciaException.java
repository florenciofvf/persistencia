package br.com.persist.plugins.persistencia;

public class PersistenciaException extends Exception {
	private static final long serialVersionUID = 1L;

	public PersistenciaException(Throwable cause) {
		super(cause);
	}

	public PersistenciaException(String message) {
		super(message);
	}
}