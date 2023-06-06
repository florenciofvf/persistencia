package br.com.persist.plugins.instrucao.cmpl;

import java.io.PrintWriter;

public class Invocacao extends No {
	public Invocacao(String nome) {
		super(nome);
	}

	@Override
	public int totalInstrucoes() {
		return 1;// TODO
	}

	@Override
	public void print(PrintWriter pw) {
		pw.print(nome);// TODO
	}
}