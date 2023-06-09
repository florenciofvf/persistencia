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

public class And extends Instrucao {
	public And(Metodo metodo) {
		super(metodo, InstrucaoConstantes.AND);
	}

	@Override
	public Instrucao clonar(Metodo metodo) {
		return new And(metodo);
	}

	@Override
	public void executar(PilhaMetodo pilhaMetodo, PilhaOperando pilhaOperando, CacheBiblioteca cacheBiblioteca)
			throws InstrucaoException {
		Object operandoD = pilhaOperando.pop();
		Object operandoE = pilhaOperando.pop();
		InstrucaoUtil.checarNumber(operandoE);
		InstrucaoUtil.checarNumber(operandoD);
		int valor = ((Number) operandoE).intValue();
		if (valor == 0) {
			pilhaOperando.push(BigInteger.valueOf(0));
			return;
		}
		valor = ((Number) operandoD).intValue();
		if (valor == 0) {
			pilhaOperando.push(BigInteger.valueOf(0));
			return;
		}
		pilhaOperando.push(BigInteger.valueOf(1));
	}
}