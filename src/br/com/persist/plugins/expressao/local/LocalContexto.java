package br.com.persist.plugins.expressao.local;

import java.io.PrintWriter;
import java.util.List;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.compilador.Context;
import br.com.persist.plugins.expressao.compilador.Contexto;
import br.com.persist.plugins.expressao.compilador.Doc;
import br.com.persist.plugins.expressao.compilador.Token;
import br.com.persist.plugins.expressao.compilador.TokenExec;
import br.com.persist.plugins.expressao.compilador.TokenManager;
import br.com.persist.plugins.expressao.funcao.FuncaoContexto;
import br.com.persist.plugins.expressao.instrucoes.ExpressaoContexto;
import br.com.persist.plugins.expressao.mapa.MapaContexto;

public class LocalContexto extends Contexto {
	public static final String INVOKE_LOCAL_MAPA = "invoke_local_mapa";
	public static final String INVOKE_LOCAL = "invoke_local";
	public static final String LOAD_LOCAL = "load_local";
	public static final String DEF_LOCAL = "def_local";
	public static final String LOCAL = "local";
	private TokenExec selecionado = new Id();

	@Override
	protected void selecionadoVia(TokenManager tokenManager, Contexto contexto) throws ExpressaoException {
		tokenManager.selecionarParentDe(this);
	}

	@Context("declaracao_local")
	@Doc("local chave = expressao;")
	@Override
	public void processar(TokenManager tokenManager, Token token) throws ExpressaoException {
		selecionado.processar(tokenManager, token);
	}

	@Override
	public boolean isDeclaracaoFuncao() throws ExpressaoException {
		Contexto expressao = getPrimeiro();
		return expressao != null && expressao.getPrimeiro() instanceof FuncaoContexto;
	}

	@Override
	public boolean isDeclaracaoMapa() throws ExpressaoException {
		Contexto expressao = getPrimeiro();
		return expressao != null && expressao.getPrimeiro() instanceof MapaContexto;
	}

	public class Id implements TokenExec {
		public void processar(TokenManager tokenManager, Token token) throws ExpressaoException {
			if (token.isChave()) {
				LocalContexto.this.token = token;
				token.setStyle(Token.DEC_LOCAL);
				selecionado = new Atribuicao();
			} else {
				tokenManager.invalidar(token);
			}
		}
	}

	class Atribuicao implements TokenExec {
		public void processar(TokenManager tokenManager, Token token) throws ExpressaoException {
			if (token.isAtribuicao()) {
				ExpressaoContexto expressao = new ExpressaoContexto(";");
				tokenManager.selecionar(expressao);
				adicionar(expressao);
			} else {
				tokenManager.invalidar(token);
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
		print(pw, DEF_LOCAL, token.getString());
	}
}