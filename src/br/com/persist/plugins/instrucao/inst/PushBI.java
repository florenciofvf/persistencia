package br.com.persist.plugins.instrucao.inst;

import java.math.BigInteger;

import br.com.persist.plugins.instrucao.InstrucaoConstantes;
import br.com.persist.plugins.instrucao.InstrucaoException;
import br.com.persist.plugins.instrucao.pro.CacheBiblioteca;
import br.com.persist.plugins.instrucao.pro.Instrucao;
import br.com.persist.plugins.instrucao.pro.Metodo;
import br.com.persist.plugins.instrucao.pro.PilhaMetodo;
import br.com.persist.plugins.instrucao.pro.PilhaOperando;

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
	public String getParam() {
		return bigInteger.toString();
	}

	@Override
	public void executar(PilhaMetodo pilhaMetodo, PilhaOperando pilhaOperando, CacheBiblioteca cacheBiblioteca)
			throws InstrucaoException {
		pilhaOperando.push(bigInteger);
	}
}