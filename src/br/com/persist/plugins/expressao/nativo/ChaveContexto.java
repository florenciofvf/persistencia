package br.com.persist.plugins.expressao.nativo;

import java.io.PrintWriter;
import java.util.List;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.compilador.Context;
import br.com.persist.plugins.expressao.compilador.Contexto;
import br.com.persist.plugins.expressao.compilador.Doc;
import br.com.persist.plugins.expressao.compilador.Indexador;
import br.com.persist.plugins.expressao.compilador.Token;
import br.com.persist.plugins.expressao.compilador.TokenManager;

public class ChaveContexto extends Contexto {
	public static final String LOAD_PARAM = "load_param";

	public ChaveContexto(Token token) {
		this.token = token;
	}

	@Context("chave")
	@Doc("chave / chave2 / chaveN")
	@Override
	public void processar(TokenManager tokenManager, Token token) throws ExpressaoException {
		tokenManager.invalidar(token);
	}

	@Override
	public void empilharLocal(List<Contexto> lista) {
		lista.add(this);
		empilharLocalNegativo(lista);
	}

	@Override
	public void listar(List<Contexto> lista) {
		lista.add(this);
		listarNegativo(lista);
	}

	@Override
	public void indexar(Indexador indexador) {
		indice = indexador.get3();
	}

	@Override
	public void salvar(PrintWriter pw) throws ExpressaoException {
		print(pw, LOAD_PARAM, token.getString());
	}
}