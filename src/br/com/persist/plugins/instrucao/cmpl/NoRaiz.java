package br.com.persist.plugins.instrucao.cmpl;

import java.io.PrintWriter;

public class NoRaiz extends No {
	public NoRaiz() {
		super(null);
	}

	@Override
	public int comprimento() {
		return 0;
	}

	@Override
	public void print(PrintWriter pw) {
		throw new IllegalStateException();
	}
}