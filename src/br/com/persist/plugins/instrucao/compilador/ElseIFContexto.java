package br.com.persist.plugins.instrucao.compilador;

import br.com.persist.plugins.instrucao.InstrucaoException;
import br.com.persist.plugins.instrucao.compilador.expressao.ExpressaoContexto;

public class ElseIFContexto extends Container {
	private final ExpressaoContexto expressao;
	private final CorpoContexto corpo;
	private boolean faseExpressao;
	private Contexto contexto;

	public ElseIFContexto() {
		contexto = Contextos.ABRE_PARENTESES;
		expressao = new ExpressaoContexto();
		corpo = new CorpoContexto();
		faseExpressao = true;
		adicionar(expressao);
		adicionar(corpo);
	}

	public ExpressaoContexto getExpressao() {
		return expressao;
	}

	public CorpoContexto getCorpo() {
		return corpo;
	}

	@Override
	public void inicializador(Compilador compilador, Token token) throws InstrucaoException {
		contexto.inicializador(compilador, token);
		if (faseExpressao) {
			compilador.setContexto(expressao);
			contexto = Contextos.ABRE_CHAVES;
			faseExpressao = false;
		} else {
			compilador.setContexto(corpo);
			corpo.setFinalizadorPai(true);
		}
	}

	@Override
	public void finalizador(Compilador compilador, Token token) throws InstrucaoException {
		compilador.setContexto(getPai());
	}

	@Override
	public String toString() {
		return "elseif >>> " + expressao.toString();
	}
}