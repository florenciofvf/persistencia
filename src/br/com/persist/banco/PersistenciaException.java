package br.com.persist.banco;

public class PersistenciaException extends Exception {
	private static final long serialVersionUID = 1L;

	public PersistenciaException(Throwable cause) {
		super(cause);
	}
}