package br.com.persist.plugins.instrucao.compilador;

import br.com.persist.plugins.instrucao.InstrucaoException;

public class ElseContexto extends Container {
	private final CorpoContexto corpo;
	private Contexto contexto;

	public ElseContexto() {
		contexto = Contextos.ABRE_CHAVES;
		corpo = new CorpoContexto();
		adicionar(corpo);
	}

	public CorpoContexto getCorpo() {
		return corpo;
	}

	@Override
	public void inicializador(Compilador compilador, Token token) throws InstrucaoException {
		contexto.inicializador(compilador, token);
		compilador.setContexto(corpo);
		corpo.setFinalizadorPai(true);
	}

	@Override
	public void finalizador(Compilador compilador, Token token) throws InstrucaoException {
		compilador.setContexto(getPai());
	}

	@Override
	public String toString() {
		return "else";
	}
}