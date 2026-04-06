package br.com.persist.plugins.expressao.salto;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.processador.Funcao;
import br.com.persist.plugins.expressao.processador.Instrucao;
import br.com.persist.plugins.expressao.processador.InstrucaoUtil;
import br.com.persist.plugins.expressao.processador.PilhaFuncao;
import br.com.persist.plugins.expressao.processador.PilhaOperando;

public class IFEqInstrucao extends Instrucao {
	private int indiceSalto;

	public IFEqInstrucao() {
		super(IFEqContexto.IF_EQ);
	}

	@Override
	public Instrucao novo() {
		return new IFEqInstrucao();
	}

	@Override
	public void setParametros(String string) {
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