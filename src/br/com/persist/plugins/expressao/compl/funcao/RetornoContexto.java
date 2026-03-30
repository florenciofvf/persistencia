package br.com.persist.plugins.expressao.compl.funcao;

import java.util.List;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.compl.Compilador;
import br.com.persist.plugins.expressao.compl.Context;
import br.com.persist.plugins.expressao.compl.Contexto;
import br.com.persist.plugins.expressao.compl.Doc;
import br.com.persist.plugins.expressao.compl.Token;
import br.com.persist.plugins.expressao.compl.TokenExec;
import br.com.persist.plugins.expressao.compl.Contexto.PontoEVirgula;
import br.com.persist.plugins.expressao.compl.instrucoes.ExpressaoContexto;

public class RetornoContexto extends Contexto {
	private TokenExec[] execs = { new PontoEVirgulaOuAbreParentese(), new PontoEVirgula() };

	@Context("retorno_da_funcao")
	@Doc({ "return;", "return expressao;" })
	@Override
	public void processar(Compilador compilador, Token token) throws ExpressaoException {
		checarIndiceEstado(compilador, execs, token);
		execs[indiceEstado].processar(compilador, token);
	}

	@Override
	protected void empilharLocalPos(List<Contexto> lista) {
		lista.add(this);
	}

	class PontoEVirgulaOuAbreParentese implements TokenExec {
		@Override
		public void processar(Compilador compilador, Token token) throws ExpressaoException {
			if (token.isPontoEVirgula()) {
				compilador.setSelecionado(parent);
				indiceEstado++;
			} else if (token.isAbreParentese()) {
				ExpressaoContexto expressao = new ExpressaoContexto(false);
				compilador.setSelecionado(expressao);
				add(expressao);
				indiceEstado++;
			} else {
				compilador.invalidar(token);
			}
		}
	}
}