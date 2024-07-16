package br.com.persist.plugins.instrucao.compilador;

import java.util.concurrent.atomic.AtomicInteger;

public class GotoContexto extends Container {
	@Override
	public void indexar(AtomicInteger atomic) {
		indice = atomic.getAndIncrement();
	}
}