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
import br.com.persist.plugins.expressao.compl.instrucoes.InstrucoesContexto;

public class FuncaoContexto extends Contexto {
	private TokenExec[] execs = { new Chave(), new IniParametros(), new TipoRetornoOuIniInstrucoes(),
			new PontoEVirgula() };
	public static final String PREFIXO_TIPO_VOID = "tipo_void";
	public static final String PREFIXO_FUNCAO = "funcao ";
	protected boolean retornoVoid;

	@Context("funcao")
	@Doc({ "funcao chave parametros instrucoes;", "funcao parametros void instrucoes;" })
	@Override
	public void processar(Compilador compilador, Token token) throws ExpressaoException {
		checarIndiceEstado(compilador, execs, token);
		execs[indiceEstado].processar(compilador, token);
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

	class TipoRetornoOuIniInstrucoes implements TokenExec {
		@Override
		public void processar(Compilador compilador, Token token) throws ExpressaoException {
			if (token.isAbreChave()) {
				InstrucoesContexto instrucoes = new InstrucoesContexto(InstrucoesContexto.FUNCAO);
				compilador.setSelecionado(instrucoes);
				add(instrucoes);
				indiceEstado++;
			} else if (ExpressaoConstantes.VOID.equals(token.getString())) {
				execs = new TokenExec[] { new Chave(), new IniParametros(), new TipoRetornoOuIniInstrucoes(),
						new AbreChave(InstrucoesContexto.FUNCAO), new PontoEVirgula() };
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
		pw.println(PREFIXO_FUNCAO + token.getString());
		if (retornoVoid) {
			pw.println(PREFIXO_TIPO_VOID);
		}
		getParametros().salvar(pw);
	}
}