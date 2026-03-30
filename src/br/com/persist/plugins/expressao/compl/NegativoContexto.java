package br.com.persist.plugins.expressao.compl;

import java.io.PrintWriter;

import br.com.persist.plugins.expressao.ExpressaoException;

public class NegativoContexto extends Contexto {
	public static final String NEG = "neg";

	@Override
	public void salvar(PrintWriter pw) throws ExpressaoException {
		print(pw, NEG);
	}
}