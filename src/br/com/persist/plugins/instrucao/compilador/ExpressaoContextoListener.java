package br.com.persist.plugins.instrucao.compilador;

import br.com.persist.plugins.instrucao.InstrucaoException;

public interface ExpressaoContextoListener {
	public void finalizador(Compilador compilador, Token token, ExpressaoContexto expressao) throws InstrucaoException;

	public void separador(Compilador compilador, Token token, ExpressaoContexto expressao) throws InstrucaoException;
}