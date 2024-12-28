package br.com.persist.plugins.instrucao.compilador;

import java.io.PrintWriter;

import br.com.persist.plugins.instrucao.InstrucaoException;

public class NegativoContexto extends Container {
	public static final String NEG = "neg";

	@Override
	public void indexar(Indexador indexador) {
		sequencia = indexador.get();
	}

	@Override
	public void salvar(Compilador compilador, PrintWriter pw) throws InstrucaoException {
		print(pw, NEG);
	}
}