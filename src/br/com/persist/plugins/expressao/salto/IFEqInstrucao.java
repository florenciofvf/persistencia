package br.com.persist.plugins.expressao.salto;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.processador.Funcao;
import br.com.persist.plugins.expressao.processador.Instrucao;
import br.com.persist.plugins.expressao.processador.InstrucaoUtil;
import br.com.persist.plugins.expressao.processador.PilhaFuncao;
import br.com.persist.plugins.expressao.processador.PilhaOperando;

public class IFEqInstrucao extends Instrucao {
	private final int indiceSalto;

	public IFEqInstrucao(int indice, String string) throws ExpressaoException {
		super(indice, IFEqContexto.IF_EQ);
		indiceSalto = Integer.parseInt(string);
	}

	@Override
	public void processar(Funcao funcao, PilhaFuncao pilhaFuncao, PilhaOperando pilhaOperando)
			throws ExpressaoException {
		Object operando = pilhaOperando.pop();
		InstrucaoUtil.checarNumber(operando);
		int valor = ((Number) operando).intValue();
		if (valor == 0) {
			funcao.setIndice(indiceSalto);
		}
	}
}