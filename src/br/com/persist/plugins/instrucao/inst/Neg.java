package br.com.persist.plugins.instrucao.inst;

import java.math.BigDecimal;
import java.math.BigInteger;

import br.com.persist.plugins.instrucao.CacheBiblioteca;
import br.com.persist.plugins.instrucao.Instrucao;
import br.com.persist.plugins.instrucao.InstrucaoConstantes;
import br.com.persist.plugins.instrucao.InstrucaoException;
import br.com.persist.plugins.instrucao.InstrucaoUtil;
import br.com.persist.plugins.instrucao.Metodo;
import br.com.persist.plugins.instrucao.PilhaMetodo;
import br.com.persist.plugins.instrucao.PilhaOperando;

public class Neg extends Instrucao {
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
			novo = ((BigInteger) operando).multiply(BigInteger.valueOf(-1));
		} else {
			novo = ((BigDecimal) operando).multiply(BigDecimal.valueOf(-1));
		}
		pilhaOperando.push(novo);
	}
}