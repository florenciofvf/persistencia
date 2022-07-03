package br.com.persist.plugins.checagem;

public class ChecagemException extends Exception {
	private static final long serialVersionUID = 1L;

	public ChecagemException(Class<?> klass, String message) {
		super(klass.getName() + ": " + message + ".");
	}
}