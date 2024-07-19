package br.com.persist.plugins.instrucao.compilador;

import java.io.PrintWriter;
import java.util.concurrent.atomic.AtomicInteger;

import br.com.persist.plugins.instrucao.InstrucaoConstantes;

public class GotoContexto extends Container {
	public static final String GOTO = "goto";
	int posicao;

	@Override
	public void indexar(AtomicInteger atomic) {
		super.indexar(atomic);
		indice = atomic.getAndIncrement();
	}

	@Override
	public void salvar(PrintWriter pw) {
		pw.print(InstrucaoConstantes.PREFIXO_INSTRUCAO + "-");
		pw.print(InstrucaoConstantes.ESPACO + GOTO);
		pw.print(InstrucaoConstantes.ESPACO + posicao);
		pw.println();
	}
}