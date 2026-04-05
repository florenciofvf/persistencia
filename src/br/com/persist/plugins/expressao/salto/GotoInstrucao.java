package br.com.persist.plugins.expressao.salto;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.processador.Funcao;
import br.com.persist.plugins.expressao.processador.Instrucao;
import br.com.persist.plugins.expressao.processador.PilhaFuncao;
import br.com.persist.plugins.expressao.processador.PilhaOperando;

public class GotoInstrucao extends Instrucao {
	private int indice;

	public GotoInstrucao() {
		super(GotoContexto.GOTO);
	}

	@Override
	public Instrucao clonar() {
		return new GotoInstrucao();
	}

	@Override
	public void setParametros(String string) {
		indice = Integer.parseInt(string);
	}

	@Override
	public void processar(Funcao funcao, PilhaFuncao pilhaFuncao, PilhaOperando pilhaOperando)
			throws ExpressaoException {
		funcao.setIndice(indice);
	}
}