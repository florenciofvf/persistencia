package br.com.persist.plugins.instrucao.compilador.constante;

import br.com.persist.plugins.instrucao.InstrucaoException;
import br.com.persist.plugins.instrucao.compilador.Compilador;
import br.com.persist.plugins.instrucao.compilador.Container;
import br.com.persist.plugins.instrucao.compilador.Token;

public class ConstanteContexto extends Container {
	private final ConstanteExpressaoContexto expressao;
	private final char[] modoPai;

	public ConstanteContexto(char[] modoPai) {
		expressao = new ConstanteExpressaoContexto();
		this.modoPai = modoPai;
		adicionar(expressao);
	}

	public ConstanteExpressaoContexto getExpressao() {
		return expressao;
	}

	@Override
	public void finalizador(Compilador compilador, Token token) throws InstrucaoException {
		if (";".equals(token.getString())) {
			compilador.setContexto(getPai());
			getPai().setModo(modoPai);
		} else {
			compilador.invalidar(token);
		}
	}
}