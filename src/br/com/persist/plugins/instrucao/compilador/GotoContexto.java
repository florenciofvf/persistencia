package br.com.persist.plugins.instrucao.compilador;

import java.io.PrintWriter;

public class GotoContexto extends Container {
	public static final String GOTO = "goto";
	int posicao;

	@Override
	public void indexar(Indexador indexador) {
		indice = indexador.get3();
	}

	@Override
	public void salvar(PrintWriter pw) {
		print(pw, GOTO, "" + posicao);
	}
}