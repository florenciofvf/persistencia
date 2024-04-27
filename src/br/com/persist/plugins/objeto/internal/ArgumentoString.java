package br.com.persist.plugins.objeto.internal;

public class ArgumentoString implements Argumento {
	private final String string;

	public ArgumentoString(String string) {
		this.string = string;
	}

	public String getString() {
		return string;
	}
}