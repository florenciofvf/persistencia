package br.com.persist.plugins.instrucao.compilador;

import br.com.persist.plugins.instrucao.InstrucaoException;
import br.com.persist.plugins.instrucao.compilador.expressao.ExpressaoContexto;

public class RetornoContexto extends Container {
	private final ExpressaoContexto expressao;
	private Contexto contexto;

	public RetornoContexto() {
		contexto = Contextos.ABRE_PARENTESES;
		expressao = new ExpressaoContexto();
		adicionar(expressao);
	}

	@Override
	public void inicializador(Compilador compilador, Token token) throws InstrucaoException {
		contexto.inicializador(compilador, token);
		contexto = Contextos.PONTO_VIRGULA;
		compilador.setContexto(expressao);
	}

	@Override
	public void finalizador(Compilador compilador, Token token) throws InstrucaoException {
		contexto.finalizador(compilador, token);
		compilador.setContexto(getPai());
	}
}