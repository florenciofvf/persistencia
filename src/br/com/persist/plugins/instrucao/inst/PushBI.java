package br.com.persist.plugins.instrucao.inst;

import java.math.BigInteger;

import br.com.persist.plugins.instrucao.CacheBiblioteca;
import br.com.persist.plugins.instrucao.Instrucao;
import br.com.persist.plugins.instrucao.InstrucaoConstantes;
import br.com.persist.plugins.instrucao.InstrucaoException;
import br.com.persist.plugins.instrucao.Metodo;
import br.com.persist.plugins.instrucao.PilhaMetodo;
import br.com.persist.plugins.instrucao.PilhaOperando;

public class PushBI extends Instrucao {
	private BigInteger bigInteger;

	public PushBI(Metodo metodo) {
		super(metodo, InstrucaoConstantes.PUSH_BIG_INTEGER);
	}

	@Override
	public Instrucao clonar(Metodo metodo) {
		PushBI resp = new PushBI(metodo);
		resp.bigInteger = bigInteger;
		return resp;
	}

	@Override
	public void setParam(String string) {
		bigInteger = new BigInteger(string);
	}

	@Override
	public void executar(PilhaMetodo pilhaMetodo, PilhaOperando pilhaOperando, CacheBiblioteca cacheBiblioteca)
			throws InstrucaoException {
		pilhaOperando.push(bigInteger);
	}
}