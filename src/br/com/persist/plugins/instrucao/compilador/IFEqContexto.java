package br.com.persist.plugins.instrucao.compilador;

import java.io.PrintWriter;
import java.util.concurrent.atomic.AtomicInteger;

public class IFEqContexto extends Container {
	public static final String IF_EQ = "ifeq";
	int posicao;

	@Override
	public void indexar(AtomicInteger atomic) {
		super.indexar(atomic);
		indice = atomic.getAndIncrement();
	}

	@Override
	public void salvar(PrintWriter pw) {
		print(pw, IF_EQ, "" + posicao);
	}
}