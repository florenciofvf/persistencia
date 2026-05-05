package br.com.persist.plugins.expressao.processador;

import br.com.persist.plugins.expressao.ExpressaoException;

public class Parametro {
	final String nome;
	final int indice;
	Object valor;

	public Parametro(int indice, String nome, Object valor) throws ExpressaoException {
		if (indice < 0) {
			throw new ExpressaoException("\u00CDndice negativo (Par\u00E2metro)");
		}
		if (nome == null || nome.trim().isEmpty()) {
			throw new ExpressaoException("Nome do par\u00E2metro inv\u00E1lido");
		}
		this.indice = indice;
		this.valor = valor;
		this.nome = nome;
	}

	public Parametro(int indice, String nome) throws ExpressaoException {
		this(indice, nome, null);
	}

	public String getNome() {
		return nome;
	}

	public int getIndice() {
		return indice;
	}

	public Parametro clonar() throws ExpressaoException {
		return new Parametro(indice, nome, valor);
	}

	@Override
	public String toString() {
		return nome + "=" + valor;
	}
}