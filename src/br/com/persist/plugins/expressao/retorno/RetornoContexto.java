package br.com.persist.plugins.expressao.retorno;

import java.io.PrintWriter;
import java.util.List;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.compilador.Context;
import br.com.persist.plugins.expressao.compilador.Contexto;
import br.com.persist.plugins.expressao.compilador.Doc;
import br.com.persist.plugins.expressao.compilador.Indexador;
import br.com.persist.plugins.expressao.compilador.Token;
import br.com.persist.plugins.expressao.compilador.TokenManager;
import br.com.persist.plugins.expressao.instrucoes.ExpressaoContexto;

public class RetornoContexto extends Contexto {
	public static final String RETURN = "return";

	@Override
	protected void selecionadoVia(TokenManager tokenManager, Contexto contexto) throws ExpressaoException {
		tokenManager.selecionarParentDe(this);
	}

	@Context("retorno_da_funcao")
	@Doc({ "return;", "return expressao;" })
	@Override
	protected void processarPre(TokenManager tokenManager, Token token) throws ExpressaoException {
		if (token.isPontoEVirgula()) {
			token.setConsumido(true);
			tokenManager.selecionarParentDe(this);
		} else {
			ExpressaoContexto expressao = new ExpressaoContexto(";");
			tokenManager.selecionar(expressao);
			adicionar(expressao);
		}
	}

	@Override
	public void processar(TokenManager tokenManager, Token token) throws ExpressaoException {
		throw new ExpressaoException("erro.processar.retorno.estado");
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
	public void indexar(Indexador indexador) {
		indice = indexador.get3();
	}

	@Override
	public void salvar(PrintWriter pw) throws ExpressaoException {
		print(pw, RETURN);
	}
}