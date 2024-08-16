package br.com.persist.assistencia;

public class AssistenciaException extends Exception {
	private static final long serialVersionUID = 1L;

	public AssistenciaException(Throwable cause) {
		super(cause);
	}

	public AssistenciaException(String message) {
		super(message);
	}
}