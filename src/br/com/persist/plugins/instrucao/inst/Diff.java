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

public class Diff extends Comparacao {
	public Diff(Metodo metodo) {
		super(metodo, InstrucaoConstantes.DIFF);
	}

	@Override
	public Instrucao clonar(Metodo metodo) {
		return new Diff(metodo);
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
				pilhaOperando.push(diferente(castBI(operandoE), castBI(operandoD)));
			} else if (operandoD instanceof BigDecimal) {
				pilhaOperando.push(diferente(createBD(castBI(operandoE)), castBD(operandoD)));
			} else {
				pilhaOperando.push(createTrue());
			}
		} else if (operandoE instanceof BigDecimal) {
			if (operandoD instanceof BigInteger) {
				pilhaOperando.push(diferente(castBD(operandoE), createBD(castBI(operandoD))));
			} else if (operandoD instanceof BigDecimal) {
				pilhaOperando.push(diferente(castBD(operandoE), castBD(operandoD)));
			} else {
				pilhaOperando.push(createTrue());
			}
		} else {
			pilhaOperando.push(differ(operandoE, operandoD));
		}
	}
}