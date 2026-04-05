package br.com.persist.plugins.expressao.compilador;

import java.util.concurrent.atomic.AtomicInteger;

public class Indexador {
	private AtomicInteger atomic;

	private int get(int delta) {
		if (atomic == null) {
			atomic = new AtomicInteger(0);
			return atomic.get();
		}
		return atomic.addAndGet(delta);
	}

	public int get1() {
		return get(1);
	}

	public int get2() {
		return get(2);
	}

	public int get3() {
		return get(3);
	}
}