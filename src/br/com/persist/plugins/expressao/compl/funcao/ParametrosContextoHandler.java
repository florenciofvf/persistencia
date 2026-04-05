package br.com.persist.plugins.expressao.compl.funcao;

import java.util.Objects;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.compl.Context;
import br.com.persist.plugins.expressao.compl.Contexto;
import br.com.persist.plugins.expressao.compl.Doc;
import br.com.persist.plugins.expressao.compl.Token;
import br.com.persist.plugins.expressao.compl.TokenExec;
import br.com.persist.plugins.expressao.compl.TokenManager;

public class ParametrosContextoHandler extends Contexto {
	private TokenExec selecionado = new Chave();
	private final String[] finalizadores;
	private Token tokenFinalizador;

	public ParametrosContextoHandler(String[] finalizadores) {
		this.finalizadores = Objects.requireNonNull(finalizadores);
	}

	public ParametrosContextoHandler(String finalizador) {
		this(new String[] { finalizador });
	}

	public ParametrosContextoHandler() {
		this(new String[] { ")" });
	}

	@Override
	protected void processarPre(TokenManager tokenManager, Token token) throws ExpressaoException {
		String string = token.getString();
		boolean finalizar = false;
		for (String item : finalizadores) {
			if (item.equals(string)) {
				finalizar = true;
			}
		}
		if (finalizar) {
			if (this.token == null) {
				tokenManager.invalidar(token);
			}
			token.setConsumido(true);
			tokenFinalizador = token;
			tokenManager.selecionarParentDe(this);
		} else if (this.token != null) {
			tokenManager.invalidar(token);
		}
	}

	public Token getTokenFinalizador() {
		return tokenFinalizador;
	}

	@Context("handler")
	@Doc("chave")
	@Override
	public void processar(TokenManager tokenManager, Token token) throws ExpressaoException {
		selecionado.processar(tokenManager, token);
	}
}