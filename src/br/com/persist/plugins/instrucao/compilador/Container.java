package br.com.persist.plugins.instrucao.compilador;

import java.util.ArrayList;
import java.util.List;

import br.com.persist.plugins.instrucao.InstrucaoException;

public abstract class Container implements Contexto {
	private final List<Container> filhos;
	protected Container pai;
	protected char[] modo;

	protected Container() {
		filhos = new ArrayList<>();
	}

	public Container getPai() {
		return pai;
	}

	public List<Container> getFilhos() {
		return filhos;
	}

	public void excluir(Container c) {
		if (c.pai == this) {
			filhos.remove(c);
			c.pai = null;
		}
	}

	public void adicionar(Container c) {
		if (c.pai != null) {
			c.pai.excluir(c);
		}
		filhos.add(c);
		c.pai = this;
	}

	protected boolean isModo(char c) {
		for (char ch : modo) {
			if (ch == c) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void inicializador(Compilador compilador, Token token) throws InstrucaoException {
		compilador.invalidar(token);
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
	public void reservado(Compilador compilador, Token token) throws InstrucaoException {
		compilador.invalidar(token);
	}

	@Override
	public void string(Compilador compilador, Token token) throws InstrucaoException {
		compilador.invalidar(token);
	}

	@Override
	public void numero(Compilador compilador, Token token) throws InstrucaoException {
		compilador.invalidar(token);
	}

	@Override
	public void identity(Compilador compilador, Token token) throws InstrucaoException {
		compilador.invalidar(token);
	}
}