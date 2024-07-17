package br.com.persist.plugins.instrucao.processador;

import java.math.BigInteger;

import br.com.persist.plugins.instrucao.InstrucaoException;
import br.com.persist.plugins.instrucao.compilador.NegativoContexto;

public class NegativoInstrucao extends Instrucao {
	public NegativoInstrucao() {
		super(NegativoContexto.NEG);
	}

	@Override
	public Instrucao clonar() {
		return this;
	}

	@Override
	public void processar(CacheBiblioteca cacheBiblioteca, Biblioteca biblioteca, Funcao funcao,
			PilhaFuncao pilhaFuncao, PilhaOperando pilhaOperando)
			throws InstrucaoException {
		Object operando = pilhaOperando.pop();
		InstrucaoUtil.checarBigIntegerBigDecimal(operando);
		Object novo;
		if (operando instanceof BigInteger) {
			novo = OperadorInstrucao.castBI(operando).negate();
		} else {
			novo = OperadorInstrucao.castBD(operando).negate();
		}
		pilhaOperando.push(novo);
	}
}