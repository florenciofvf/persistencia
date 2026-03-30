package br.com.persist.plugins.expressao.compl.nativo;

import java.io.PrintWriter;
import java.util.List;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.compl.Compilador;
import br.com.persist.plugins.expressao.compl.Context;
import br.com.persist.plugins.expressao.compl.Contexto;
import br.com.persist.plugins.expressao.compl.Doc;
import br.com.persist.plugins.expressao.compl.Indexador;
import br.com.persist.plugins.expressao.compl.Token;

public class InteiroContexto extends Contexto {
	public static final String PUSH_BIG_INTEGER = "push_big_integer";

	public InteiroContexto(Token token) {
		super(token);
	}

	@Context("inteiro")
	@Doc("123")
	@Override
	public void processar(Compilador compilador, Token token) throws ExpressaoException {
		compilador.invalidar(token);
	}

	@Override
	public void indexar(Indexador indexador) {
		indice = indexador.get2();
		// indexarNegativo(indexador);
	}

	@Override
	public void empilharLocal(List<Contexto> lista) {
		lista.add(this);
		empilharLocalNegativo(lista);
	}

	@Override
	protected void listar(List<Contexto> lista) {
		lista.add(this);
		listarNegativo(lista);
	}

	@Override
	public void salvar(PrintWriter pw) throws ExpressaoException {
		print(pw, PUSH_BIG_INTEGER, token.getString());
		// salvarNegativo(pw);
	}
}