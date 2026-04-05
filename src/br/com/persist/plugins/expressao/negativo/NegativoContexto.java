package br.com.persist.plugins.expressao.negativo;

import java.io.PrintWriter;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.compilador.Contexto;

public class NegativoContexto extends Contexto {
	public static final String NEG = "neg";

	@Override
	public void salvar(PrintWriter pw) throws ExpressaoException {
		print(pw, NEG);
	}
}