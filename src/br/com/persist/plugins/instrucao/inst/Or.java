package br.com.persist.plugins.instrucao.inst;

import br.com.persist.plugins.instrucao.InstrucaoConstantes;
import br.com.persist.plugins.instrucao.InstrucaoException;
import br.com.persist.plugins.instrucao.pro.CacheBiblioteca;
import br.com.persist.plugins.instrucao.pro.Instrucao;
import br.com.persist.plugins.instrucao.pro.Metodo;
import br.com.persist.plugins.instrucao.pro.PilhaMetodo;
import br.com.persist.plugins.instrucao.pro.PilhaOperando;

public class Or extends Logico {
	public Or(Metodo metodo) {
		super(metodo, InstrucaoConstantes.OR);
	}

	@Override
	public Instrucao clonar(Metodo metodo) {
		return new Or(metodo);
	}

	@Override
	public void executar(PilhaMetodo pilhaMetodo, PilhaOperando pilhaOperando, CacheBiblioteca cacheBiblioteca)
			throws InstrucaoException {
		Object operandoD = pilhaOperando.pop();
		Object operandoE = pilhaOperando.pop();
		InstrucaoUtil.checarNumber(operandoE);
		InstrucaoUtil.checarNumber(operandoD);
		int valor = ((Number) operandoE).intValue();
		if (valor != 0) {
			pilhaOperando.push(createTrue());
			return;
		}
		valor = ((Number) operandoD).intValue();
		if (valor != 0) {
			pilhaOperando.push(createTrue());
			return;
		}
		pilhaOperando.push(createFalse());
	}
}