package br.com.persist.plugins.instrucao.inst;

import java.math.BigDecimal;
import java.math.BigInteger;

import br.com.persist.plugins.instrucao.InstrucaoConstantes;
import br.com.persist.plugins.instrucao.InstrucaoException;
import br.com.persist.plugins.instrucao.pro.CacheBiblioteca;
import br.com.persist.plugins.instrucao.pro.Instrucao;
import br.com.persist.plugins.instrucao.pro.Metodo;
import br.com.persist.plugins.instrucao.pro.PilhaMetodo;
import br.com.persist.plugins.instrucao.pro.PilhaOperando;

public class Add extends Matemat {
	public Add(Metodo metodo) {
		super(metodo, InstrucaoConstantes.ADD);
	}

	@Override
	public Instrucao clonar(Metodo metodo) {
		return new Add(metodo);
	}

	@Override
	public void executar(PilhaMetodo pilhaMetodo, PilhaOperando pilhaOperando, CacheBiblioteca cacheBiblioteca)
			throws InstrucaoException {
		Object operandoD = pilhaOperando.pop();
		Object operandoE = pilhaOperando.pop();
		InstrucaoUtil.checarOperando(operandoE);
		InstrucaoUtil.checarOperando(operandoD);
		if (operandoE instanceof BigInteger) {
			if (operandoD instanceof BigInteger) {
				pilhaOperando.push(castBI(operandoE).add(castBI(operandoD)));
			} else if (operandoD instanceof BigDecimal) {
				pilhaOperando.push(createBD(castBI(operandoE)).add(castBD(operandoD)));
			} else {
				pilhaOperando.push(operandoE.toString() + operandoD.toString());
			}
		} else if (operandoE instanceof BigDecimal) {
			if (operandoD instanceof BigInteger) {
				pilhaOperando.push(castBD(operandoE).add(createBD(castBI(operandoD))));
			} else if (operandoD instanceof BigDecimal) {
				pilhaOperando.push(castBD(operandoE).add(castBD(operandoD)));
			} else {
				pilhaOperando.push(operandoE.toString() + operandoD.toString());
			}
		} else {
			pilhaOperando.push(operandoE.toString() + operandoD.toString());
		}
	}
}