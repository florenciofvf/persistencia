package br.com.persist.plugins.instrucao.inst;

import java.math.BigInteger;

import br.com.persist.plugins.instrucao.InstrucaoConstantes;
import br.com.persist.plugins.instrucao.InstrucaoException;
import br.com.persist.plugins.instrucao.pro.CacheBiblioteca;
import br.com.persist.plugins.instrucao.pro.Instrucao;
import br.com.persist.plugins.instrucao.pro.Metodo;
import br.com.persist.plugins.instrucao.pro.PilhaMetodo;
import br.com.persist.plugins.instrucao.pro.PilhaOperando;

public class Neg extends Matemat {
	public Neg(Metodo metodo) {
		super(metodo, InstrucaoConstantes.NEG);
	}

	@Override
	public Instrucao clonar(Metodo metodo) {
		return new Neg(metodo);
	}

	@Override
	public void executar(PilhaMetodo pilhaMetodo, PilhaOperando pilhaOperando, CacheBiblioteca cacheBiblioteca)
			throws InstrucaoException {
		Object operando = pilhaOperando.pop();
		InstrucaoUtil.checarBigIntegerBigDecimal(operando);
		Object novo;
		if (operando instanceof BigInteger) {
			novo = castBI(operando).negate();
		} else {
			novo = castBD(operando).negate();
		}
		pilhaOperando.push(novo);
	}
}