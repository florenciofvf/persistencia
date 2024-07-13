package br.com.persist.plugins.instrucao.compilador;

import br.com.persist.plugins.instrucao.InstrucaoException;

public class RetornoContexto extends Container {
	public RetornoContexto() {
		contexto = Contextos.ABRE_PARENTESES;
		adicionar(new ExpressaoContexto());
	}

	public ExpressaoContexto getExpressao() {
		return (ExpressaoContexto) get(0);
	}

	@Override
	public void inicializador(Compilador compilador, Token token) throws InstrucaoException {
		contexto.inicializador(compilador, token);
		contexto = Contextos.PONTO_VIRGULA;
		compilador.setContexto(getExpressao());
	}

	@Override
	public void finalizador(Compilador compilador, Token token) throws InstrucaoException {
		contexto.finalizador(compilador, token);
		compilador.setContexto(getPai());
	}

	@Override
	public String toString() {
		return "return >>> " + getExpressao().toString();
	}
}