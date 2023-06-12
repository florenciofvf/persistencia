package br.com.persist.plugins.instrucao.inst;

import java.math.BigInteger;

import br.com.persist.plugins.instrucao.InstrucaoConstantes;
import br.com.persist.plugins.instrucao.InstrucaoException;
import br.com.persist.plugins.instrucao.pro.CacheBiblioteca;
import br.com.persist.plugins.instrucao.pro.Instrucao;
import br.com.persist.plugins.instrucao.pro.Metodo;
import br.com.persist.plugins.instrucao.pro.PilhaMetodo;
import br.com.persist.plugins.instrucao.pro.PilhaOperando;

public class MaiorI extends Comparacao {
	public MaiorI(Metodo metodo) {
		super(metodo, InstrucaoConstantes.MAIOR_I);
	}

	@Override
	public Instrucao clonar(Metodo metodo) {
		return new MaiorI(metodo);
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
				pilhaOperando.push(maiorI(castBI(operandoE), castBI(operandoD)));
			} else {
				pilhaOperando.push(maiorI(createBD(castBI(operandoE)), castBD(operandoD)));
			}
		} else {
			if (operandoD instanceof BigInteger) {
				pilhaOperando.push(maiorI(castBD(operandoE), createBD(castBI(operandoD))));
			} else {
				pilhaOperando.push(maiorI(castBD(operandoE), castBD(operandoD)));
			}
		}
	}
}