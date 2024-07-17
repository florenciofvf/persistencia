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

public class Igual extends Comparacao {
	public Igual(Metodo metodo) {
		super(metodo, InstrucaoConstantes.IGUAL);
	}

	@Override
	public Instrucao clonar(Metodo metodo) {
		return new Igual(metodo);
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
				pilhaOperando.push(igual(castBI(operandoE), castBI(operandoD)));
			} else if (operandoD instanceof BigDecimal) {
				pilhaOperando.push(igual(createBD(castBI(operandoE)), castBD(operandoD)));
			} else {
				pilhaOperando.push(createFalse());
			}
		} else if (operandoE instanceof BigDecimal) {
			if (operandoD instanceof BigInteger) {
				pilhaOperando.push(igual(castBD(operandoE), createBD(castBI(operandoD))));
			} else if (operandoD instanceof BigDecimal) {
				pilhaOperando.push(igual(castBD(operandoE), castBD(operandoD)));
			} else {
				pilhaOperando.push(createFalse());
			}
		} else {
			pilhaOperando.push(equals(operandoE, operandoD));
		}
	}
}