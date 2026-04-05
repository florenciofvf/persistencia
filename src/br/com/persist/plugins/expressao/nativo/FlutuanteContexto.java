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

public class FlutuanteContexto extends Contexto {
	public static final String PUSH_FLUTUANTE = "push_flutuante";

	public FlutuanteContexto(Token token) {
		super(token);
	}

	@Context("flutuante")
	@Doc("123.123")
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
		indice = indexador.get2();
	}

	@Override
	public void salvar(PrintWriter pw) throws ExpressaoException {
		print(pw, PUSH_FLUTUANTE, token.getString());
	}
}