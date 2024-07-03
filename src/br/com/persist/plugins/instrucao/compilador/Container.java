package br.com.persist.plugins.instrucao.compilador;

import java.util.ArrayList;
import java.util.List;

import br.com.persist.plugins.instrucao.InstrucaoException;

public abstract class Container implements Contexto {
	private final List<Container> filhos;
	protected Container pai;

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

	protected void throwInstrucaoException(Token token) throws InstrucaoException {
		throw new InstrucaoException(token.string, false);
	}
}