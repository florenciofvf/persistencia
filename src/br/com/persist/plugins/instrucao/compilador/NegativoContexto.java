package br.com.persist.plugins.instrucao.compilador;

import java.io.PrintWriter;
import java.util.concurrent.atomic.AtomicInteger;

public class NegativoContexto extends Container {
	public static final String NEG = "neg";

	@Override
	public void indexar(AtomicInteger atomic) {
		indice = atomic.getAndIncrement();
	}

	@Override
	public void salvar(PrintWriter pw) {
		print(pw, NEG);
	}
}