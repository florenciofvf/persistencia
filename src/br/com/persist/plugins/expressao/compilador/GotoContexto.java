package br.com.persist.plugins.expressao.compilador;

import br.com.persist.plugins.expressao.ExpressaoException;

public class GotoContexto extends Contexto {
	public static final String GOTO = "goto";
	private Contexto destino;

	public Contexto getDestino() {
		return destino;
	}

	public void setDestino(Contexto destino) throws ExpressaoException {
		if(destino == null) {
			throw new ExpressaoException("erro.goto.ponto_salto_nulo");
		}
		this.destino = destino;
	}
}