package br.com.persist.plugins.objeto.internal;

import java.util.List;

public class Argumento {
	private final List<Object[]> argumentosArray;
	private final String argumentosString;
	private final boolean argString;
	private final byte lengthArray;

	public Argumento(List<Object[]> argumentosArray, String argumentosString, byte lengthArray, boolean argString) {
		this.argumentosString = argumentosString;
		this.argumentosArray = argumentosArray;
		this.lengthArray = lengthArray;
		this.argString = argString;
	}

	public List<Object[]> getArgumentosArray() {
		return argumentosArray;
	}

	public String getArgumentosString() {
		return argumentosString;
	}

	public boolean isArgString() {
		return argString;
	}

	public byte getLengthArray() {
		return lengthArray;
	}
}