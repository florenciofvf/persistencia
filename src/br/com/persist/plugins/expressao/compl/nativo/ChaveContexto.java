package br.com.persist.plugins.expressao.compl.nativo;

import java.util.List;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.compl.Compilador;
import br.com.persist.plugins.expressao.compl.Context;
import br.com.persist.plugins.expressao.compl.Contexto;
import br.com.persist.plugins.expressao.compl.Doc;
import br.com.persist.plugins.expressao.compl.Token;

public class ChaveContexto extends Contexto {
	public ChaveContexto(Token token) {
		this.token = token;
	}

	@Context("chave")
	@Doc("chave / chave2 / chaveN")
	@Override
	public void processar(Compilador compilador, Token token) throws ExpressaoException {
		compilador.invalidar(token);
	}

	@Override
	public void empilharLocal(List<Contexto> lista) {
		lista.add(this);
	}

	@Override
	public void listar(List<Contexto> lista) {
		lista.add(this);
	}
}