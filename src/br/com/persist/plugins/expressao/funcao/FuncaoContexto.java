package br.com.persist.plugins.expressao.funcao;

import java.io.PrintWriter;

import br.com.persist.plugins.expressao.ExpressaoConstantes;
import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.compilador.Context;
import br.com.persist.plugins.expressao.compilador.Contexto;
import br.com.persist.plugins.expressao.compilador.Doc;
import br.com.persist.plugins.expressao.compilador.Token;
import br.com.persist.plugins.expressao.compilador.TokenExec;
import br.com.persist.plugins.expressao.compilador.TokenManager;
import br.com.persist.plugins.expressao.instrucoes.InstrucoesContexto;
import br.com.persist.plugins.expressao.parametros.ParametrosContexto;

public class FuncaoContexto extends Contexto {
	private TokenExec[] execs = { new Chave(), new IniParametros(), new TipoRetornoOuIniInstrucoes() };
	public static final String PREFIXO_TIPO_VOID = "tipo_void";
	public static final String PREFIXO_FUNCAO = "funcao ";
	protected boolean retornoVoid;

	@Override
	protected void selecionadoVia(TokenManager tokenManager, Contexto contexto) throws ExpressaoException {
		if (contexto instanceof InstrucoesContexto) {
			tokenManager.selecionarParentDe(this);
		}
	}

	@Context("funcao")
	@Doc({ "funcao chave parametros instrucoes", "funcao chave parametros void instrucoes" })
	@Override
	public void processar(TokenManager tokenManager, Token token) throws ExpressaoException {
		checarIndiceEstado(tokenManager, execs, token);
		execs[indiceEstado].processar(tokenManager, token);
	}

	class IniParametros implements TokenExec {
		public void processar(TokenManager tokenManager, Token token) throws ExpressaoException {
			if (token.isAbreParentese()) {
				ParametrosContexto parametros = new ParametrosContexto();
				tokenManager.selecionar(parametros);
				adicionar(parametros);
				indiceEstado++;
			} else {
				tokenManager.invalidar(token);
			}
		}
	}

	class TipoRetornoOuIniInstrucoes implements TokenExec {
		@Override
		public void processar(TokenManager tokenManager, Token token) throws ExpressaoException {
			if (token.isAbreChave()) {
				InstrucoesContexto instrucoes = new InstrucoesContexto();
				tokenManager.selecionar(instrucoes);
				adicionar(instrucoes);
				indiceEstado++;
			} else if (ExpressaoConstantes.VOID.equals(token.getString())) {
				execs = new TokenExec[] { new Chave(), new IniParametros(), new TipoRetornoOuIniInstrucoes(),
						new IniInstrucoes() };
				retornoVoid = true;
				indiceEstado++;
			} else {
				tokenManager.invalidar(token);
			}
		}
	}

	public ParametrosContexto getParametros() {
		return (ParametrosContexto) getPrimeiro();
	}

	@Override
	public void salvar(PrintWriter pw) throws ExpressaoException {
		pw.println(PREFIXO_FUNCAO + token.getString());
		if (retornoVoid) {
			pw.println(PREFIXO_TIPO_VOID);
		}
		getParametros().salvar(pw);
	}
}