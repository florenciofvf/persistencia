package br.com.persist.plugins.expressao.mapa;

import java.io.PrintWriter;
import java.util.List;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.compilador.Context;
import br.com.persist.plugins.expressao.compilador.Contexto;
import br.com.persist.plugins.expressao.compilador.Token;
import br.com.persist.plugins.expressao.compilador.TokenManager;

public class PutItemMapaContexto extends Contexto {
	public static final String PUT_ITEM_MAPA = "put_item_mapa";

	public PutItemMapaContexto(Token token) {
		super(token);
	}

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
		print(pw, PUT_ITEM_MAPA, token.getString());
	}
}