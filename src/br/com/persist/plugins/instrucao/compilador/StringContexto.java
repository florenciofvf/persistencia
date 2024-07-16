package br.com.persist.plugins.instrucao.compilador;

import java.util.concurrent.atomic.AtomicInteger;

public class StringContexto extends Container {
	private final String string;

	public StringContexto(Token token) {
		this.string = token.getString();
		this.token = token;
	}

	public String getString() {
		return string;
	}

	@Override
	public void indexar(AtomicInteger atomic) {
		indice = atomic.getAndIncrement();
	}

	@Override
	public String toString() {
		return string;
	}
}