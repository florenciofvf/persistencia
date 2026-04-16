package br.com.persist.plugins.expressao.funcao;

import java.io.PrintWriter;
import java.util.Map;

import br.com.persist.plugins.expressao.ExpressaoConstantes;
import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.biblioteca.BibliotecaContexto;
import br.com.persist.plugins.expressao.compilador.Context;
import br.com.persist.plugins.expressao.compilador.Contexto;
import br.com.persist.plugins.expressao.compilador.Doc;
import br.com.persist.plugins.expressao.compilador.Token;
import br.com.persist.plugins.expressao.compilador.TokenExec;
import br.com.persist.plugins.expressao.compilador.TokenManager;
import br.com.persist.plugins.expressao.parametros.ParametroContexto;
import br.com.persist.plugins.expressao.parametros.ParametrosContexto;

public class FuncaoNativaContexto extends Contexto implements IFuncaoContexto {
	private TokenExec[] execs = { new ChaveN(), new Chave(), new IniParametros(), new TipoRetornoOuPontoEVirgula(),
			new PontoEVirgula() };
	public static final String PREFIXO_FUNCAO_NATIVA = "funcao_nativa ";
	public static final String PREFIXO_TIPO_VOID = "tipo_void";
	public static final String DEFUN_NATIVE = "defun_native";
	protected boolean retornoVoid;
	protected Token biblioteca;

	@Context("funcao_nativa")
	@Doc({ "funcao_nativa biblioteca chave parametros;", "funcao_nativa biblioteca chave parametros void;" })
	@Override
	public void processar(TokenManager tokenManager, Token token) throws ExpressaoException {
		checarIndiceEstado(tokenManager, execs, token);
		execs[indiceEstado].processar(tokenManager, token);
	}

	class ChaveN implements TokenExec {
		public void processar(TokenManager tokenManager, Token token) throws ExpressaoException {
			if (token.isChaveN()) {
				biblioteca = token;
				indiceEstado++;
			} else {
				tokenManager.invalidar(token);
			}
		}
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

	class TipoRetornoOuPontoEVirgula implements TokenExec {
		@Override
		public void processar(TokenManager tokenManager, Token token) throws ExpressaoException {
			if (token.isPontoEVirgula()) {
				execs = new TokenExec[] { new ChaveN(), new Chave(), new IniParametros(),
						new TipoRetornoOuPontoEVirgula() };
				tokenManager.selecionarParentDe(FuncaoNativaContexto.this);
				indiceEstado++;
			} else if (VOID.equals(token.getString())) {
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
		pw.println(PREFIXO_FUNCAO_NATIVA + biblioteca.getString() + ExpressaoConstantes.ESPACO + token.getString());
		if (retornoVoid) {
			pw.println(PREFIXO_TIPO_VOID);
		}
		getParametros().salvar(pw);
	}

	@Override
	public BibliotecaContexto getBibliotecaContexto() {
		return (BibliotecaContexto) parent;
	}

	@Override
	public String getNome() {
		return token.getString();
	}

	@Override
	public Map<String, ParametroContexto> getMapaParametros() {
		return getParametros().getMapaParametros();
	}

	@Override
	public void configurarChaveParametro() {
		configurarChaveParametro(getMapaParametros());
	}
}