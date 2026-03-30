package br.com.persist.plugins.expressao.compl.invocacao;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.compl.Compilador;
import br.com.persist.plugins.expressao.compl.Context;
import br.com.persist.plugins.expressao.compl.Contexto;
import br.com.persist.plugins.expressao.compl.Doc;
import br.com.persist.plugins.expressao.compl.Token;

public class ParametroContexto extends Contexto {
	protected final Token chave;

	public ParametroContexto(Token chave) {
		this.chave = chave;
	}

	@Context("parametro")
	@Doc("chave")
	@Override
	public void processar(Compilador compilador, Token token) throws ExpressaoException {
		compilador.invalidar(token);
	}
}