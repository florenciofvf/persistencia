package br.com.persist.plugins.expressao.compl.funcao;

import java.io.PrintWriter;

import br.com.persist.plugins.expressao.ExpressaoConstantes;
import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.compl.Compilador;
import br.com.persist.plugins.expressao.compl.Context;
import br.com.persist.plugins.expressao.compl.Contexto;
import br.com.persist.plugins.expressao.compl.Doc;
import br.com.persist.plugins.expressao.compl.Token;
import br.com.persist.plugins.expressao.compl.TokenExec;

public class FuncaoNativaContexto extends Contexto {
	private TokenExec[] execs = { new ChaveN(), new Chave(), new IniParametros(), new TipoRetornoOuPontoEVirgula(),
			new PontoEVirgula() };
	public static final String PREFIXO_FUNCAO_NATIVA = "funcao_nativa ";
	public static final String PREFIXO_TIPO_VOID = "tipo_void";
	protected boolean retornoVoid;
	protected Token biblioteca;

	@Context("funcao_nativa")
	@Doc({ "funcao_nativa biblioteca chave parametros;", "funcao_nativa biblioteca chave parametros void;" })
	@Override
	public void processar(Compilador compilador, Token token) throws ExpressaoException {
		checarIndiceEstado(compilador, execs, token);
		execs[indiceEstado].processar(compilador, token);
	}

	class ChaveN implements TokenExec {
		public void processar(Compilador compilador, Token token) throws ExpressaoException {
			if (token.isChaveN()) {
				biblioteca = token;
				indiceEstado++;
			} else {
				compilador.invalidar(token);
			}
		}
	}

	class IniParametros implements TokenExec {
		public void processar(Compilador compilador, Token token) throws ExpressaoException {
			if (token.isAbreParentese()) {
				ParametrosContexto parametros = new ParametrosContexto();
				compilador.setSelecionado(parametros);
				add(parametros);
				indiceEstado++;
			} else {
				compilador.invalidar(token);
			}
		}
	}

	class TipoRetornoOuPontoEVirgula implements TokenExec {
		@Override
		public void processar(Compilador compilador, Token token) throws ExpressaoException {
			if (token.isPontoEVirgula()) {
				execs = new TokenExec[] { new ChaveN(), new Chave(), new IniParametros(),
						new TipoRetornoOuPontoEVirgula() };
				compilador.setSelecionado(parent);
				indiceEstado++;
			} else if (ExpressaoConstantes.VOID.equals(token.getString())) {
				retornoVoid = true;
				indiceEstado++;
			} else {
				compilador.invalidar(token);
			}
		}
	}

	public ParametrosContexto getParametros() {
		return (ParametrosContexto) getPrimeiro();
	}

	@Override
	public void salvar(PrintWriter pw) throws ExpressaoException {
		pw.println(PREFIXO_FUNCAO_NATIVA + biblioteca.getString() + ExpressaoConstantes.ESPACO + token.getString());
		if (retornoVoid) {
			pw.println(PREFIXO_TIPO_VOID);
		}
		getParametros().salvar(pw);
	}
}