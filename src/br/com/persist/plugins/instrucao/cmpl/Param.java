package br.com.persist.plugins.instrucao.cmpl;

import java.io.PrintWriter;

public class Param extends No {
	public Param(String nome) {
		super(nome);
	}

	@Override
	public int comprimento() {
		return 1;
	}

	@Override
	public void print(PrintWriter pw) {
		pw.print(nome);
	}
}