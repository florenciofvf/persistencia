package br.com.persist.data;

public class DataException extends Exception {
	private static final long serialVersionUID = 1L;

	public DataException(String message) {
		super(message + ".");
	}
}