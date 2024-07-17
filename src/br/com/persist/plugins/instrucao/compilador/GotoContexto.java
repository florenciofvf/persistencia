package br.com.persist.plugins.instrucao.compilador;

import java.io.PrintWriter;
import java.util.concurrent.atomic.AtomicInteger;

public class GotoContexto extends Container {
	public static final String GOTO = "goto";
	int posicao;

	@Override
	public void indexar(AtomicInteger atomic) {
		indice = atomic.getAndIncrement();
	}

	@Override
	public void salvar(PrintWriter pw) {
		super.salvar(pw);
		print(pw, GOTO, "" + posicao);
	}
}