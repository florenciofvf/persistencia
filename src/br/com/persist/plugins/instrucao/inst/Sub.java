package br.com.persist.plugins.instrucao.inst;

import java.math.BigInteger;

import br.com.persist.plugins.instrucao.CacheBiblioteca;
import br.com.persist.plugins.instrucao.Instrucao;
import br.com.persist.plugins.instrucao.InstrucaoConstantes;
import br.com.persist.plugins.instrucao.InstrucaoException;
import br.com.persist.plugins.instrucao.InstrucaoUtil;
import br.com.persist.plugins.instrucao.Metodo;
import br.com.persist.plugins.instrucao.PilhaMetodo;
import br.com.persist.plugins.instrucao.PilhaOperando;

public class Sub extends Matemat {
	public Sub(Metodo metodo) {
		super(metodo, InstrucaoConstantes.SUB);
	}

	@Override
	public Instrucao clonar(Metodo metodo) {
		return new Sub(metodo);
	}

	@Override
	public void executar(PilhaMetodo pilhaMetodo, PilhaOperando pilhaOperando, CacheBiblioteca cacheBiblioteca)
			throws InstrucaoException {
		Object operandoD = pilhaOperando.pop();
		Object operandoE = pilhaOperando.pop();
		InstrucaoUtil.checarBigIntegerBigDecimal(operandoE);
		InstrucaoUtil.checarBigIntegerBigDecimal(operandoD);
		if (operandoE instanceof BigInteger) {
			if (operandoD instanceof BigInteger) {
				pilhaOperando.push(castBI(operandoE).subtract(castBI(operandoD)));
			} else {
				pilhaOperando.push(createBD(castBI(operandoE)).subtract(castBD(operandoD)));
			}
		} else {
			if (operandoD instanceof BigInteger) {
				pilhaOperando.push(castBD(operandoE).subtract(createBD(castBI(operandoD))));
			} else {
				pilhaOperando.push(castBD(operandoE).subtract(castBD(operandoD)));
			}
		}
	}
}