package br.com.persist.plugins.expressao.compl.nativo;

import java.util.List;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.compl.Compilador;
import br.com.persist.plugins.expressao.compl.Context;
import br.com.persist.plugins.expressao.compl.Contexto;
import br.com.persist.plugins.expressao.compl.Doc;
import br.com.persist.plugins.expressao.compl.Token;

public class StringContexto extends Contexto {
	public StringContexto(Token token) {
		this.token = token;
	}

	@Context("string")
	@Doc("'xyz'")
	@Override
	public void processar(Compilador compilador, Token token) throws ExpressaoException {
		compilador.invalidar(token);
	}

	@Override
	public void empilharLocal(List<Contexto> lista) {
		lista.add(this);
	}
}