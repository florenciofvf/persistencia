package br.com.persist.plugins.instrucao.compilador;

import java.io.PrintWriter;

public class IFEqContexto extends Container {
	public static final String IF_EQ = "ifeq";
	int posicao;

	@Override
	public void salvar(PrintWriter pw) {
		super.salvar(pw);
		print(pw, IF_EQ, "" + posicao);
	}
}