package br.com.persist.plugins.instrucao.compilador;

import java.io.PrintWriter;

import br.com.persist.plugins.instrucao.InstrucaoException;

public abstract class AbstratoContexto implements Contexto {
	@Override
	public void inicializador(Compilador compilador, Token token) throws InstrucaoException {
		compilador.invalidar(token);
	}

	@Override
	public void antesFinalizador(Compilador compilador, Token token) throws InstrucaoException {
	}

	@Override
	public void finalizador(Compilador compilador, Token token) throws InstrucaoException {
		compilador.invalidar(token);
	}

	@Override
	public void separador(Compilador compilador, Token token) throws InstrucaoException {
		compilador.invalidar(token);
	}

	@Override
	public void operador(Compilador compilador, Token token) throws InstrucaoException {
		compilador.invalidar(token);
	}

	@Override
	public void antesReservado(Compilador compilador, Token token) throws InstrucaoException {
	}

	@Override
	public void reservado(Compilador compilador, Token token) throws InstrucaoException {
		compilador.invalidar(token);
	}

	@Override
	public void lambda(Compilador compilador, Token token) throws InstrucaoException {
		compilador.invalidar(token);
	}

	@Override
	public void string(Compilador compilador, Token token) throws InstrucaoException {
		compilador.invalidar(token);
	}

	@Override
	public void lista(Compilador compilador, Token token) throws InstrucaoException {
		compilador.invalidar(token);
	}

	@Override
	public void mapa(Compilador compilador, Token token) throws InstrucaoException {
		compilador.invalidar(token);
	}

	@Override
	public void numero(Compilador compilador, Token token) throws InstrucaoException {
		compilador.invalidar(token);
	}

	@Override
	public void antesIdentity(Compilador compilador, Token token) throws InstrucaoException {
	}

	@Override
	public void identity(Compilador compilador, Token token) throws InstrucaoException {
		compilador.invalidar(token);
	}

	public void salvar(Compilador compilador, PrintWriter pw) throws InstrucaoException {
	}
}