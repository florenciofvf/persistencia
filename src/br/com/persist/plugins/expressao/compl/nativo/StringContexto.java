package br.com.persist.plugins.expressao.compl.nativo;

import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.compl.Compilador;
import br.com.persist.plugins.expressao.compl.Context;
import br.com.persist.plugins.expressao.compl.Contexto;
import br.com.persist.plugins.expressao.compl.Doc;
import br.com.persist.plugins.expressao.compl.Indexador;
import br.com.persist.plugins.expressao.compl.Token;

public class StringContexto extends Contexto {
	public static final String PUSH_STRING = "push_string";

	public StringContexto(Token token) {
		super(token);
	}

	@Context("string")
	@Doc("'xyz'")
	@Override
	public void processar(Compilador compilador, Token token) throws ExpressaoException {
		compilador.invalidar(token);
	}

	@Override
	public void indexar(Indexador indexador) {
		indice = indexador.get2();
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
	public void salvar(PrintWriter pw) throws ExpressaoException {
		print(pw, PUSH_STRING, new String(token.getString().getBytes(), StandardCharsets.UTF_8));
	}
}