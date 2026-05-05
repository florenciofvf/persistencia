package br.com.persist.plugins.expressao.salto;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.processador.Funcao;
import br.com.persist.plugins.expressao.processador.Instrucao;
import br.com.persist.plugins.expressao.processador.PilhaFuncao;
import br.com.persist.plugins.expressao.processador.PilhaOperando;

public class GotoInstrucao extends Instrucao {
	private final int indiceSalto;

	public GotoInstrucao(int indice, String string) throws ExpressaoException {
		super(indice, GotoContexto.GOTO);
		indiceSalto = Integer.parseInt(string);
	}

	@Override
	public void processar(Funcao funcao, PilhaFuncao pilhaFuncao, PilhaOperando pilhaOperando)
			throws ExpressaoException {
		funcao.setIndice(indiceSalto);
	}

	@Override
	public String toString() {
		return super.toString() + " " + indiceSalto;
	}
}