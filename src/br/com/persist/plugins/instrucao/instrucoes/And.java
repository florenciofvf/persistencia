package br.com.persist.plugins.instrucao.inst;

import br.com.persist.plugins.instrucao.InstrucaoConstantes;
import br.com.persist.plugins.instrucao.InstrucaoException;
import br.com.persist.plugins.instrucao.pro.CacheBiblioteca;
import br.com.persist.plugins.instrucao.pro.Instrucao;
import br.com.persist.plugins.instrucao.pro.Metodo;
import br.com.persist.plugins.instrucao.pro.PilhaMetodo;
import br.com.persist.plugins.instrucao.pro.PilhaOperando;

public class And extends Logico {
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
			pilhaOperando.push(createFalse());
			return;
		}
		valor = ((Number) operandoD).intValue();
		if (valor == 0) {
			pilhaOperando.push(createFalse());
			return;
		}
		pilhaOperando.push(createTrue());
	}
}