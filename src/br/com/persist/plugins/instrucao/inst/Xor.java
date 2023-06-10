package br.com.persist.plugins.instrucao.inst;

import br.com.persist.plugins.instrucao.InstrucaoConstantes;
import br.com.persist.plugins.instrucao.InstrucaoException;
import br.com.persist.plugins.instrucao.pro.CacheBiblioteca;
import br.com.persist.plugins.instrucao.pro.Instrucao;
import br.com.persist.plugins.instrucao.pro.Metodo;
import br.com.persist.plugins.instrucao.pro.PilhaMetodo;
import br.com.persist.plugins.instrucao.pro.PilhaOperando;

public class Xor extends Logico {
	public Xor(Metodo metodo) {
		super(metodo, InstrucaoConstantes.XOR);
	}

	@Override
	public Instrucao clonar(Metodo metodo) {
		return new Xor(metodo);
	}

	@Override
	public void executar(PilhaMetodo pilhaMetodo, PilhaOperando pilhaOperando, CacheBiblioteca cacheBiblioteca)
			throws InstrucaoException {
		Object operandoD = pilhaOperando.pop();
		Object operandoE = pilhaOperando.pop();
		InstrucaoUtil.checarNumber(operandoE);
		InstrucaoUtil.checarNumber(operandoD);
		int valorE = ((Number) operandoE).intValue();
		int valorD = ((Number) operandoD).intValue();
		if ((valorE == 0 && valorD != 0) || (valorE != 0 && valorD == 0)) {
			pilhaOperando.push(createTrue());
		} else {
			pilhaOperando.push(createFalse());
		}
	}
}