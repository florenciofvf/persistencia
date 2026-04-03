package br.com.persist.plugins.expressao.compl.funcao;

import java.io.PrintWriter;
import java.util.List;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.compl.Compilador;
import br.com.persist.plugins.expressao.compl.Context;
import br.com.persist.plugins.expressao.compl.Contexto;
import br.com.persist.plugins.expressao.compl.Doc;
import br.com.persist.plugins.expressao.compl.Token;
import br.com.persist.plugins.expressao.compl.TokenExec;
import br.com.persist.plugins.expressao.compl.instrucoes.ExpressaoContexto;

public class RetornoContexto extends Contexto {
	private TokenExec[] execs = { new PontoEVirgulaOuAbreParentese(), new PontoEVirgula() };
	public static final String RETURN = "return";

	@Context("retorno_da_funcao")
	@Doc({ "return;", "return expressao;" })
	@Override
	public void processar(Compilador compilador, Token token) throws ExpressaoException {
		checarIndiceEstado(compilador, execs, token);
		execs[indiceEstado].processar(compilador, token);
	}

	class PontoEVirgulaOuAbreParentese implements TokenExec {
		@Override
		public void processar(Compilador compilador, Token token) throws ExpressaoException {
			if (token.isPontoEVirgula()) {
				compilador.selecionarParentDe(RetornoContexto.this);
				indiceEstado++;
			} else if (token.isAbreParentese()) {
				ExpressaoContexto expressao = new ExpressaoContexto();
				compilador.selecionar(expressao);
				add(expressao);
				indiceEstado++;
			} else {
				compilador.invalidar(token);
			}
		}
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
		print(pw, RETURN);
	}
}