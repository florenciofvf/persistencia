package br.com.persist.plugins.expressao.compl;

import java.util.List;

import br.com.persist.plugins.expressao.ExpressaoException;

public class ChaveContexto extends Contexto {
	public ChaveContexto(Token token) {
		this.token = token;
	}

	@Context("chave")
	@Doc("chave / chave2")
	@Override
	public void processar(Compilador compilador, Token token) throws ExpressaoException {
		compilador.invalidar(token);
	}

	@Override
	public void empilharLocal(List<Contexto> lista) {
		lista.add(this);
	}
}