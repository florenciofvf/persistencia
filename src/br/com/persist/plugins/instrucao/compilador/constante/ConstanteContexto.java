package br.com.persist.plugins.instrucao.compilador.constante;

import br.com.persist.plugins.instrucao.InstrucaoException;
import br.com.persist.plugins.instrucao.compilador.Compilador;
import br.com.persist.plugins.instrucao.compilador.Container;
import br.com.persist.plugins.instrucao.compilador.Token;

public class ConstanteContexto extends Container {
	private final ConstanteExpressaoContexto expressao;

	public ConstanteContexto() {
		expressao = new ConstanteExpressaoContexto();
	}

	public ConstanteExpressaoContexto getExpressao() {
		return expressao;
	}

	@Override
	public void finalizador(Compilador compilador, Token token) throws InstrucaoException {
		if (isModo('F')) {
			if (";".equals(token.getString())) {
				compilador.setContexto(getPai());
			} else {
				compilador.invalidar(token);
			}
		} else {
			compilador.invalidar(token);
		}
	}
}