package br.com.persist.plugins.instrucao.processador;

import java.math.BigInteger;

import br.com.persist.plugins.instrucao.InstrucaoException;
import br.com.persist.plugins.instrucao.compilador.NumeroContexto;

public class PushBigIntegerInstrucao extends Instrucao {
	private BigInteger bigInteger;

	public PushBigIntegerInstrucao() {
		super(NumeroContexto.PUSH_BIG_INTEGER);
	}

	@Override
	public Instrucao clonar() {
		return new PushBigIntegerInstrucao();
	}

	@Override
	public void setParametros(String string) {
		bigInteger = new BigInteger(string);
	}

	@Override
	public void processar(CacheBiblioteca cacheBiblioteca, Biblioteca biblioteca, Funcao funcao,
			PilhaFuncao pilhaFuncao, PilhaOperando pilhaOperando) throws InstrucaoException {
		pilhaOperando.push(bigInteger);
	}
}