package br.com.persist.plugins.instrucao.compilador;

import java.util.concurrent.atomic.AtomicInteger;

public class Indexador {
	private AtomicInteger atomic = new AtomicInteger(0);

	public int value() {
		return atomic.get();
	}

	public int get() {
		return atomic.getAndIncrement();
	}

	public int get2() {
		return atomic.getAndAdd(2);
	}

	public int get3() {
		return atomic.getAndAdd(3);
	}
}