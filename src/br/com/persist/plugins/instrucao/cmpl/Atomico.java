package br.com.persist.plugins.instrucao.cmpl;

import java.io.PrintWriter;

public class Atomico extends No {
	public Atomico(String nome) {
		super(nome);
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