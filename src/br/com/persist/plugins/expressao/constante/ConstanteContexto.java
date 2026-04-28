package br.com.persist.plugins.expressao.constante;

import java.io.PrintWriter;
import java.util.List;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.compilador.Context;
import br.com.persist.plugins.expressao.compilador.Contexto;
import br.com.persist.plugins.expressao.compilador.Doc;
import br.com.persist.plugins.expressao.compilador.Token;
import br.com.persist.plugins.expressao.compilador.TokenExec;
import br.com.persist.plugins.expressao.compilador.TokenManager;
import br.com.persist.plugins.expressao.instrucoes.ExpressaoContexto;

public class ConstanteContexto extends Contexto {
	public static final String INVOKE_CONST = "invoke_const";
	public static final String LOAD_CONST = "load_const";
	public static final String DEF_CONST = "def_const";
	public static final String CONST = "const";
	private TokenExec selecionado = new Id();

	@Override
	protected void selecionadoVia(TokenManager tokenManager, Contexto contexto) throws ExpressaoException {
		tokenManager.selecionarParentDe(this);
	}

	@Context("declaracao_constante")
	@Doc("const chave = expressao;")
	@Override
	public void processar(TokenManager tokenManager, Token token) throws ExpressaoException {
		selecionado.processar(tokenManager, token);
	}

	public class Id implements TokenExec {
		public void processar(TokenManager tokenManager, Token token) throws ExpressaoException {
			if (token.isChave()) {
				ConstanteContexto.this.token = token;
				token.setStyle(Token.CONSTANTE);
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
		print(pw, DEF_CONST, token.getString());
	}
}