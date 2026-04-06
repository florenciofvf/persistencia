package br.com.persist.plugins.expressao.negativo;

import java.math.BigInteger;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.operador.OperadorInstrucao;
import br.com.persist.plugins.expressao.processador.Funcao;
import br.com.persist.plugins.expressao.processador.Instrucao;
import br.com.persist.plugins.expressao.processador.InstrucaoUtil;
import br.com.persist.plugins.expressao.processador.PilhaFuncao;
import br.com.persist.plugins.expressao.processador.PilhaOperando;

public class NegativoInstrucao extends Instrucao {
	public NegativoInstrucao() {
		super(NegativoContexto.NEG);
	}

	@Override
	public Instrucao novo() {
		return new NegativoInstrucao();
	}

	@Override
	public void processar(Funcao funcao, PilhaFuncao pilhaFuncao, PilhaOperando pilhaOperando)
			throws ExpressaoException {
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