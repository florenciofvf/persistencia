package br.com.persist.plugins.instrucao.compilador.invocacao;

import br.com.persist.plugins.instrucao.InstrucaoException;
import br.com.persist.plugins.instrucao.compilador.Compilador;
import br.com.persist.plugins.instrucao.compilador.Container;
import br.com.persist.plugins.instrucao.compilador.Token;

public class InvocarContexto extends Container {
	private final Container container;
	protected boolean inicializado;

	public InvocarContexto(Container c) {
		this.container = c;
	}

	public Container getContainer() {
		return container;
	}

	public boolean isInicializado() {
		return inicializado;
	}

	public void setInicializado(boolean inicializado) {
		this.inicializado = inicializado;
	}

	@Override
	public void finalizador(Compilador compilador, Token token) throws InstrucaoException {
		if (")".equals(token.getString())) {
			compilador.setContexto(getPai());
		} else {
			compilador.invalidar(token);
		}
	}
}