package br.com.persist.plugins.instrucao.cmpl;

import java.io.PrintWriter;

public class Expressao extends No {
	private final boolean negarExpressao;

	public Expressao(boolean negarExpressao) {
		super(null);
		this.negarExpressao = negarExpressao;
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