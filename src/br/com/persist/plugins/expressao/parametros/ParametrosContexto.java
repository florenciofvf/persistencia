package br.com.persist.plugins.expressao.parametros;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.biblioteca.BibliotecaContexto;
import br.com.persist.plugins.expressao.compilador.Context;
import br.com.persist.plugins.expressao.compilador.Contexto;
import br.com.persist.plugins.expressao.compilador.Doc;
import br.com.persist.plugins.expressao.compilador.Token;
import br.com.persist.plugins.expressao.compilador.TokenManager;
import br.com.persist.plugins.expressao.funcao.IFuncaoContexto;

public class ParametrosContexto extends Contexto {
	private static final String ERRO_EXPRESSAO_PARAMETROS_SELECIONADO_VIA = "erro.expressao.parametros.selecionado_via";
	private static final String[] FINALIZADORES = new String[] { ",", ")" };

	@Override
	protected void selecionadoVia(TokenManager tokenManager, Contexto contexto) throws ExpressaoException {
		if (contexto instanceof ParametrosContextoHandler) {
			ParametrosContextoHandler handler = (ParametrosContextoHandler) contexto;
			Token finalizador = handler.getTokenFinalizador();
			if (finalizador == null) {
				throw new ExpressaoException(ERRO_EXPRESSAO_PARAMETROS_SELECIONADO_VIA);
			}
			if (handler.getToken() == null) {
				throw new ExpressaoException(ERRO_EXPRESSAO_PARAMETROS_SELECIONADO_VIA);
			}
			remove(handler);
			adicionar(new ParametroContexto(handler.getToken()));
			if (finalizador.isVirgula()) {
				handler = new ParametrosContextoHandler(FINALIZADORES);
				tokenManager.selecionar(handler);
				adicionar(handler);
			} else if (finalizador.isFechaParentese()) {
				tokenManager.selecionarParentDe(this);
			} else {
				throw new ExpressaoException(ERRO_EXPRESSAO_PARAMETROS_SELECIONADO_VIA);
			}
		} else {
			throw new ExpressaoException(ERRO_EXPRESSAO_PARAMETROS_SELECIONADO_VIA);
		}
	}

	@Override
	public void adicionar(Contexto c) throws ExpressaoException {
		if (c instanceof ParametroContexto && contemParametro((ParametroContexto) c)) {
			IFuncaoContexto funcao = getIFuncaoContexto();
			BibliotecaContexto biblio = funcao.getBibliotecaContexto();
			throw new ExpressaoException("erro.parametro_duplicado", c.getToken().getString(), funcao.getNome(),
					biblio.getNome());
		}
		super.adicionar(c);
	}

	private boolean contemParametro(ParametroContexto param) {
		for (Contexto item : componentes) {
			if (item instanceof ParametroContexto && ((ParametroContexto) item).igual(param)) {
				return true;
			}
		}
		return false;
	}

	private IFuncaoContexto getIFuncaoContexto() {
		return (IFuncaoContexto) parent;
	}

	public Map<String, ParametroContexto> getMapaParametros() {
		Map<String, ParametroContexto> resp = new HashMap<>();
		for (Contexto item : componentes) {
			if (item instanceof ParametroContexto) {
				ParametroContexto param = (ParametroContexto) item;
				resp.put(param.getNome(), param);
			}
		}
		return resp;
	}

	@Override
	protected void processarPre(TokenManager tokenManager, Token token) throws ExpressaoException {
		if (token.isFechaParentese()) {
			if (isEmpty()) {
				token.setConsumido(true);
				tokenManager.selecionarParentDe(this);
			} else {
				tokenManager.invalidar(token);
			}
		} else {
			ParametrosContextoHandler handler = new ParametrosContextoHandler(FINALIZADORES);
			tokenManager.selecionar(handler);
			adicionar(handler);
		}
	}

	@Context("parametros_da_funcao")
	@Doc({ "()", "param", "parametros_da_funcao, param" })
	@Override
	public void processar(TokenManager tokenManager, Token token) throws ExpressaoException {
		throw new ExpressaoException("erro.processar.parametros.estado");
	}

	@Override
	public void salvar(PrintWriter pw) throws ExpressaoException {
		for (Contexto item : componentes) {
			item.salvar(pw);
		}
	}
}