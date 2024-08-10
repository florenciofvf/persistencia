package br.com.persist.plugins.instrucao.compilador;

import java.io.PrintWriter;

public class NegativoContexto extends Container {
	public static final String NEG = "neg";

	@Override
	public void indexar(Indexador indexador) {
		indice = indexador.get();
	}

	@Override
	public void salvar(PrintWriter pw) {
		print(pw, NEG);
	}
}