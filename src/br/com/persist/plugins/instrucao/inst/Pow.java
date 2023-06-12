package br.com.persist.plugins.instrucao.inst;

import java.math.BigInteger;

import br.com.persist.plugins.instrucao.InstrucaoConstantes;
import br.com.persist.plugins.instrucao.InstrucaoException;
import br.com.persist.plugins.instrucao.pro.CacheBiblioteca;
import br.com.persist.plugins.instrucao.pro.Instrucao;
import br.com.persist.plugins.instrucao.pro.Metodo;
import br.com.persist.plugins.instrucao.pro.PilhaMetodo;
import br.com.persist.plugins.instrucao.pro.PilhaOperando;

public class Pow extends Matemat {
	public Pow(Metodo metodo) {
		super(metodo, InstrucaoConstantes.POW);
	}

	@Override
	public Instrucao clonar(Metodo metodo) {
		return new Pow(metodo);
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
				pilhaOperando.push(castBI(operandoE).pow(castBI(operandoD).intValue()));
			} else {
				pilhaOperando.push(createBD(castBI(operandoE)).pow(castBD(operandoD).intValue()));
			}
		} else {
			if (operandoD instanceof BigInteger) {
				pilhaOperando.push(castBD(operandoE).pow(createBD(castBI(operandoD)).intValue()));
			} else {
				pilhaOperando.push(castBD(operandoE).pow(castBD(operandoD).intValue()));
			}
		}
	}
}