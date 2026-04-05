package br.com.persist.plugins.expressao.nativo;

import java.math.BigInteger;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.processador.Funcao;
import br.com.persist.plugins.expressao.processador.Instrucao;
import br.com.persist.plugins.expressao.processador.PilhaFuncao;
import br.com.persist.plugins.expressao.processador.PilhaOperando;

public class InteiroPushInstrucao extends Instrucao {
	private BigInteger bigInteger;

	public InteiroPushInstrucao() {
		super(InteiroContexto.PUSH_INTEIRO);
	}

	@Override
	public Instrucao clonar() {
		return new InteiroPushInstrucao();
	}

	@Override
	public void setParametros(String string) {
		bigInteger = new BigInteger(string);
	}

	@Override
	public void processar(Funcao funcao, PilhaFuncao pilhaFuncao, PilhaOperando pilhaOperando)
			throws ExpressaoException {
		pilhaOperando.push(bigInteger);
	}
}