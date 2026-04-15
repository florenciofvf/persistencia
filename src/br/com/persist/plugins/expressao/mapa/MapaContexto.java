package br.com.persist.plugins.expressao.mapa;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.compilador.Context;
import br.com.persist.plugins.expressao.compilador.Contexto;
import br.com.persist.plugins.expressao.compilador.Doc;
import br.com.persist.plugins.expressao.compilador.Token;
import br.com.persist.plugins.expressao.compilador.Token.Tipo;
import br.com.persist.plugins.expressao.compilador.TokenExec;
import br.com.persist.plugins.expressao.compilador.TokenManager;
import br.com.persist.plugins.expressao.instrucoes.ExpressaoContexto;
import br.com.persist.plugins.expressao.invocacao.InvocacaoContexto;

public class MapaContexto extends Contexto {
	private static final String ERRO_EXPRESSAO_MAPA_SELECIONADO_VIA = "erro.expressao.mapa.selecionado_via";
	private static final String[] FINALIZADORES = new String[] { ",", "}" };
	private TokenExec selecionado = new CampoMapa();
	private Token ultimoCampo;

	public MapaContexto() {
		adicionar2(new InvocacaoContexto(new Token("map.create", Tipo.VIRTUAL, -1), true));
	}

	@Override
	protected void selecionadoVia(TokenManager tokenManager, Contexto contexto) throws ExpressaoException {
		if (contexto instanceof ExpressaoContexto) {
			ExpressaoContexto expressao = (ExpressaoContexto) contexto;
			Token token = expressao.getTokenFinalizador();
			if (token == null) {
				throw new ExpressaoException(ERRO_EXPRESSAO_MAPA_SELECIONADO_VIA);
			}
			if (token.isVirgula()) {
				selecionado = new CampoMapa();
			} else if (token.isFechaChave()) {
				tokenManager.selecionarParentDe(this);
			} else {
				throw new ExpressaoException(ERRO_EXPRESSAO_MAPA_SELECIONADO_VIA);
			}
		} else {
			throw new ExpressaoException(ERRO_EXPRESSAO_MAPA_SELECIONADO_VIA);
		}
	}

	@Override
	protected void processarPre(TokenManager tokenManager, Token token) throws ExpressaoException {
		if (token.isFechaChave()) {
			if (isEmpty()) {
				token.setConsumido(true);
				tokenManager.selecionarParentDe(this);
			} else {
				tokenManager.invalidar(token);
			}
		}
	}

	@Context("mapa")
	@Doc({ "{}", "{'chave':1}", "{...}" })
	@Override
	public void processar(TokenManager tokenManager, Token token) throws ExpressaoException {
		selecionado.processar(tokenManager, token);
	}

	class CampoMapa implements TokenExec {
		public void processar(TokenManager tokenManager, Token token) throws ExpressaoException {
			if (token.isString()) {
				ultimoCampo = token;
				selecionado = new Separador();
			} else {
				tokenManager.invalidar(token);
			}
		}
	}

	class Separador implements TokenExec {
		public void processar(TokenManager tokenManager, Token token) throws ExpressaoException {
			if (token.isOperador() && ":".equals(token.getString())) {
				ExpressaoContexto expressao = new ExpressaoContexto(FINALIZADORES);
				tokenManager.selecionar(expressao);
				adicionar(expressao);
				adicionarPutItemMapaContexto(ultimoCampo);
			} else {
				tokenManager.invalidar(token);
			}
		}

		private void adicionarPutItemMapaContexto(Token token) throws ExpressaoException {
			adicionar(new PutItemMapaContexto(token));
		}
	}
}