package br.com.persist.plugins.instrucao.compilador;

import java.util.concurrent.atomic.AtomicInteger;

public class NumeroContexto extends Container {
	private final String numero;

	public NumeroContexto(Token token) {
		this.numero = token.getString();
		this.token = token;
	}

	public String getNumero() {
		return numero;
	}

	@Override
	public void indexar(AtomicInteger atomic) {
		super.indexar(atomic);
		indice = atomic.getAndIncrement();
	}

	@Override
	public String toString() {
		return numero;
	}
}