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

public class Xor extends Instrucao {
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
		if ((valorE == 0 && valorD > 0) || (valorE > 0 && valorD == 0)) {
			pilhaOperando.push(BigInteger.valueOf(1));
		} else {
			pilhaOperando.push(BigInteger.valueOf(0));
		}
	}
}