package br.com.persist.plugins.expressao.compilador;

import br.com.persist.plugins.expressao.ExpressaoException;

public class Compilador {
	private Contexto selecionado;

	public void invalidar(Token token, String chaveMsg) throws ExpressaoException {

	}

	public void invalidar(Token token) throws ExpressaoException {

	}

	public void setSelecionado(Contexto selecionado) {
		this.selecionado = selecionado;
	}

	public Contexto getSelecionado() {
		return selecionado;
	}
}