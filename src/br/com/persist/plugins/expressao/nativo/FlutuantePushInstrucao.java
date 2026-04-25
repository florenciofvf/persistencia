package br.com.persist.plugins.expressao.nativo;

import java.math.BigDecimal;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.processador.Funcao;
import br.com.persist.plugins.expressao.processador.Instrucao;
import br.com.persist.plugins.expressao.processador.PilhaFuncao;
import br.com.persist.plugins.expressao.processador.PilhaOperando;

public class FlutuantePushInstrucao extends Instrucao {
	private final BigDecimal bigDecimal;

	public FlutuantePushInstrucao(int indice, String parametros) throws ExpressaoException {
		super(indice, FlutuanteContexto.PUSH_FLUTUANTE);
		bigDecimal = new BigDecimal(parametros);
	}

	@Override
	public void processar(Funcao funcao, PilhaFuncao pilhaFuncao, PilhaOperando pilhaOperando)
			throws ExpressaoException {
		pilhaOperando.push(bigDecimal);
	}
}