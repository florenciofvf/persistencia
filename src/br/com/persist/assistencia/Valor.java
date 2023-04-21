package br.com.persist.assistencia;

import java.util.Objects;

public class Valor {
	private final String string;
	private final boolean numerico;

	public Valor(String string) {
		this.string = Objects.requireNonNull(string);
		numerico = ehNumero(string);
	}

	public String getString() {
		return string;
	}

	public boolean isNumerico() {
		return numerico;
	}

	private static boolean ehNumero(String string) {
		if (Util.estaVazio(string)) {
			return false;
		}
		for (char c : string.toCharArray()) {
			if (c < '0' || c > '9') {
				return false;
			}
		}
		return true;
	}
}