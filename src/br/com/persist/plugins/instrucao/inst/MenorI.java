package br.com.persist.plugins.instrucao.inst;

import java.math.BigInteger;

import br.com.persist.plugins.instrucao.InstrucaoConstantes;
import br.com.persist.plugins.instrucao.InstrucaoException;
import br.com.persist.plugins.instrucao.pro.CacheBiblioteca;
import br.com.persist.plugins.instrucao.pro.Instrucao;
import br.com.persist.plugins.instrucao.pro.Metodo;
import br.com.persist.plugins.instrucao.pro.PilhaMetodo;
import br.com.persist.plugins.instrucao.pro.PilhaOperando;

public class MenorI extends Comparacao {
	public MenorI(Metodo metodo) {
		super(metodo, InstrucaoConstantes.MENOR_I);
	}

	@Override
	public Instrucao clonar(Metodo metodo) {
		return new MenorI(metodo);
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
				pilhaOperando.push(menorI(castBI(operandoE), castBI(operandoD)));
			} else {
				pilhaOperando.push(menorI(createBD(castBI(operandoE)), castBD(operandoD)));
			}
		} else {
			if (operandoD instanceof BigInteger) {
				pilhaOperando.push(menorI(castBD(operandoE), createBD(castBI(operandoD))));
			} else {
				pilhaOperando.push(menorI(castBD(operandoE), castBD(operandoD)));
			}
		}
	}
}