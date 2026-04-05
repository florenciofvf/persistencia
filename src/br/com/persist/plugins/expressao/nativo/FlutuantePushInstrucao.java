package br.com.persist.plugins.expressao.nativo;

import java.math.BigDecimal;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.processador.Funcao;
import br.com.persist.plugins.expressao.processador.Instrucao;
import br.com.persist.plugins.expressao.processador.PilhaFuncao;
import br.com.persist.plugins.expressao.processador.PilhaOperando;

public class FlutuantePushInstrucao extends Instrucao {
	private BigDecimal bigDecimal;

	public FlutuantePushInstrucao() {
		super(FlutuanteContexto.PUSH_FLUTUANTE);
	}

	@Override
	public Instrucao clonar() {
		return new FlutuantePushInstrucao();
	}

	@Override
	public void setParametros(String string) {
		bigDecimal = new BigDecimal(string);
	}

	@Override
	public void processar(Funcao funcao, PilhaFuncao pilhaFuncao, PilhaOperando pilhaOperando)
			throws ExpressaoException {
		pilhaOperando.push(bigDecimal);
	}
}