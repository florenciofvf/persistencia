package br.com.persist.plugins.expressao.compl.biblio;

import java.io.PrintWriter;
import java.util.List;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.compl.Compilador;
import br.com.persist.plugins.expressao.compl.Context;
import br.com.persist.plugins.expressao.compl.Contexto;
import br.com.persist.plugins.expressao.compl.Doc;
import br.com.persist.plugins.expressao.compl.Token;
import br.com.persist.plugins.expressao.compl.TokenExec;

public class ConstanteContexto extends Contexto {
	private TokenExec[] execs = { new Chave(), new Atribuicao(), new AbreParentese(), new PontoEVirgula() };
	public static final String DEF_CONST = "def_const";

	@Context("declaracao_constante")
	@Doc("const chave = expressao;")
	@Override
	public void processar(Compilador compilador, Token token) throws ExpressaoException {
		checarIndiceEstado(compilador, execs, token);
		execs[indiceEstado].processar(compilador, token);
	}

	class Atribuicao implements TokenExec {
		public void processar(Compilador compilador, Token token) throws ExpressaoException {
			if (token.isAtribuicao()) {
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
		print(pw, DEF_CONST, token.getString());
	}
}