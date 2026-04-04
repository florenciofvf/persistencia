package br.com.persist.plugins.expressao.compl.funcao;

import java.io.PrintWriter;
import java.util.List;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.compl.Compilador;
import br.com.persist.plugins.expressao.compl.Context;
import br.com.persist.plugins.expressao.compl.Contexto;
import br.com.persist.plugins.expressao.compl.Doc;
import br.com.persist.plugins.expressao.compl.Indexador;
import br.com.persist.plugins.expressao.compl.Token;
import br.com.persist.plugins.expressao.compl.instrucoes.ExpressaoContexto;

public class RetornoContexto extends Contexto {
	public static final String RETURN = "return";

	@Override
	protected void selecionarParentDeApos(Compilador compilador, Contexto contexto) throws ExpressaoException {
		compilador.selecionarParentDe(RetornoContexto.this);
	}

	@Context("retorno_da_funcao")
	@Doc({ "return;", "return expressao;" })
	@Override
	protected void processarPre(Compilador compilador, Token token) throws ExpressaoException {
		if (token.isPontoEVirgula()) {
			token.setConsumido(true);
			compilador.selecionarParentDe(RetornoContexto.this);
		} else {
			ExpressaoContexto expressao = new ExpressaoContexto(";");
			compilador.selecionar(expressao);
			add(expressao);
		}
	}

	@Override
	public void processar(Compilador compilador, Token token) throws ExpressaoException {
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