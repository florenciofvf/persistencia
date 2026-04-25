package br.com.persist.plugins.expressao.nativo;

import java.math.BigInteger;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.processador.Funcao;
import br.com.persist.plugins.expressao.processador.Instrucao;
import br.com.persist.plugins.expressao.processador.PilhaFuncao;
import br.com.persist.plugins.expressao.processador.PilhaOperando;

public class InteiroPushInstrucao extends Instrucao {
	private final BigInteger bigInteger;

	public InteiroPushInstrucao(int indice, String string) throws ExpressaoException {
		super(indice, InteiroContexto.PUSH_INTEIRO);
		bigInteger = new BigInteger(string);
	}

	@Override
	public void processar(Funcao funcao, PilhaFuncao pilhaFuncao, PilhaOperando pilhaOperando)
			throws ExpressaoException {
		pilhaOperando.push(bigInteger);
	}
}