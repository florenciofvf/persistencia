package br.com.persist.plugins.expressao.parametros;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.processador.Funcao;
import br.com.persist.plugins.expressao.processador.Instrucao;
import br.com.persist.plugins.expressao.processador.PilhaFuncao;
import br.com.persist.plugins.expressao.processador.PilhaOperando;

public class ParametroLoadInstrucao extends Instrucao {
	public ParametroLoadInstrucao() {
		super(ParametroContexto.LOAD_PARAM);
	}

	@Override
	public Instrucao clonar() {
		return new ParametroLoadInstrucao();
	}

	@Override
	public void processar(Funcao funcao, PilhaFuncao pilhaFuncao, PilhaOperando pilhaOperando)
			throws ExpressaoException {
		Object valor = funcao.getValorParametro(parametros);
		/*
		 * if (valor instanceof Funcao) { valor = ((Funcao) valor).clonar(); }
		 */
		pilhaOperando.push(valor);
	}
}