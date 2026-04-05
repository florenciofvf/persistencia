package br.com.persist.plugins.expressao.retorno;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.processador.Funcao;
import br.com.persist.plugins.expressao.processador.Instrucao;
import br.com.persist.plugins.expressao.processador.PilhaFuncao;
import br.com.persist.plugins.expressao.processador.PilhaOperando;

public class RetornoInstrucao extends Instrucao {
	public RetornoInstrucao() {
		super(RetornoContexto.RETURN);
	}

	@Override
	public Instrucao clonar() {
		return new RetornoInstrucao();
	}

	@Override
	public void processar(Funcao funcao, PilhaFuncao pilhaFuncao, PilhaOperando pilhaOperando)
			throws ExpressaoException {
		pilhaFuncao.pop();
	}
}