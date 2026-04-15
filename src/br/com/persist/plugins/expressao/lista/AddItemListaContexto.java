package br.com.persist.plugins.expressao.lista;

import java.io.PrintWriter;
import java.util.List;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.compilador.Context;
import br.com.persist.plugins.expressao.compilador.Contexto;
import br.com.persist.plugins.expressao.compilador.Token;
import br.com.persist.plugins.expressao.compilador.TokenManager;

public class AddItemListaContexto extends Contexto {
	public static final String ADD_ITEM_LISTA = "add_item_lista";

	@Context("operador")
	@Override
	public void processar(TokenManager tokenManager, Token token) throws ExpressaoException {
		tokenManager.invalidar(token);
	}

	@Override
	protected void empilharLocalPos(List<Contexto> lista) {
		lista.add(this);
	}

	@Override
	protected void listarPos(List<Contexto> lista) {
		lista.add(this);
	}

	@Override
	public void salvar(PrintWriter pw) throws ExpressaoException {
		print(pw, ADD_ITEM_LISTA);
	}
}