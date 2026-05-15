package br.com.persist.plugins.expressao.nativo;

import java.math.BigInteger;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.processador.Funcao;
import br.com.persist.plugins.expressao.processador.Instrucao;
import br.com.persist.plugins.expressao.processador.PilhaFuncao;
import br.com.persist.plugins.expressao.processador.PilhaOperando;
import br.com.persist.plugins.expressao.processador.Push;

public class InteiroPushInstrucao extends Instrucao implements Push {
	private final BigInteger bigInteger;

	public InteiroPushInstrucao(int indice, String parametros) throws ExpressaoException {
		super(indice, InteiroContexto.PUSH_INTEIRO);
		bigInteger = new BigInteger(parametros);
	}

	@Override
	public void processar(Funcao funcao, PilhaFuncao pilhaFuncao, PilhaOperando pilhaOperando)
			throws ExpressaoException {
		pilhaOperando.push(bigInteger);
	}

	@Override
	public String toString() {
		return super.toString() + " " + bigInteger;
	}
}