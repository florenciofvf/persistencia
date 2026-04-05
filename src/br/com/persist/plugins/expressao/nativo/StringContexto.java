package br.com.persist.plugins.expressao.nativo;

import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.compilador.Context;
import br.com.persist.plugins.expressao.compilador.Contexto;
import br.com.persist.plugins.expressao.compilador.Doc;
import br.com.persist.plugins.expressao.compilador.Indexador;
import br.com.persist.plugins.expressao.compilador.Token;
import br.com.persist.plugins.expressao.compilador.TokenManager;

public class StringContexto extends Contexto {
	public static final String PUSH_STRING = "push_string";

	public StringContexto(Token token) {
		super(token);
	}

	@Context("string")
	@Doc("'xyz'")
	@Override
	public void processar(TokenManager tokenManager, Token token) throws ExpressaoException {
		tokenManager.invalidar(token);
	}

	@Override
	public void empilharLocal(List<Contexto> lista) {
		lista.add(this);
	}

	@Override
	public void listar(List<Contexto> lista) {
		lista.add(this);
	}

	@Override
	public void indexar(Indexador indexador) {
		indice = indexador.get3();
	}

	@Override
	public void salvar(PrintWriter pw) throws ExpressaoException {
		print(pw, PUSH_STRING, new String(token.getString().getBytes(), StandardCharsets.UTF_8));
	}
}