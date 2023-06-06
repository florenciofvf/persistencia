package br.com.persist.plugins.instrucao.cmpl;

import java.io.PrintWriter;

public class Variavel extends No {
	private final boolean negarVariavel;

	public Variavel(String nome, boolean negarVariavel) {
		super(nome);
		this.negarVariavel = negarVariavel;
	}

	@Override
	public int totalInstrucoes() {
		return 1;
	}

	@Override
	public void print(PrintWriter pw) {
		pw.print(nome);
	}
}