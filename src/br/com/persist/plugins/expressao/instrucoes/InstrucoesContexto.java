package br.com.persist.plugins.expressao.instrucoes;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.compilador.Contexto;
import br.com.persist.plugins.expressao.compilador.Token;
import br.com.persist.plugins.expressao.compilador.TokenManager;
import br.com.persist.plugins.expressao.condicional.IFContexto;
import br.com.persist.plugins.expressao.constante.ConstanteContexto;
import br.com.persist.plugins.expressao.funcao.FuncaoContexto;
import br.com.persist.plugins.expressao.invocacao.InvocacaoContexto;
import br.com.persist.plugins.expressao.local.LocalContexto;
import br.com.persist.plugins.expressao.loop.WhileContexto;
import br.com.persist.plugins.expressao.retorno.RetornoContexto;

public class InstrucoesContexto extends Salto {
	private final boolean incondicional;

	public InstrucoesContexto(boolean incondicional) {
		this.incondicional = incondicional;
	}

	@Override
	public void processar(TokenManager tokenManager, Token token) throws ExpressaoException {
		if (token.isReservado()) {
			if (ConstanteContexto.CONST.equals(token.getString())) {
				ConstanteContexto constante = new ConstanteContexto();
				tokenManager.selecionar(constante);
				adicionar(constante);
			} else if (LocalContexto.LOCAL.equals(token.getString())) {
				LocalContexto local = new LocalContexto();
				tokenManager.selecionar(local);
				adicionar(local);
			} else if (RetornoContexto.RETURN.equals(token.getString())) {
				RetornoContexto retorno = new RetornoContexto();
				tokenManager.selecionar(retorno);
				adicionar(retorno);
			} else if (IFContexto.IF.equals(token.getString())) {
				IFContexto se = new IFContexto();
				tokenManager.selecionar(se);
				adicionar(se);
			} else if (WhileContexto.WHILE.equals(token.getString())) {
				WhileContexto loop = new WhileContexto();
				tokenManager.selecionar(loop);
				adicionar(loop);
			} else {
				tokenManager.invalidar(token);
			}
		} else if (token.chave()) {
			InvocacaoContexto invocacao = new InvocacaoContexto(token, false);
			tokenManager.selecionar(invocacao);
			adicionar(invocacao);
		} else if (token.isFechaChave()) {
			if ((parent instanceof FuncaoContexto) && !retornoGarantido()) {
				FuncaoContexto fn = (FuncaoContexto) parent;
				throw new ExpressaoException("erro.funcao.sem_retorno", fn.getNome());
			}
			tokenManager.selecionarParentDe(this);
		} else {
			tokenManager.invalidar(token);
		}
	}

	@Override
	protected void configurarSaltosPos() throws ExpressaoException {
		if (parent instanceof WhileContexto) {
			instrucoesGotoWhile();
		} else if (parent instanceof IFContexto) {
			instrucoesGotoIf();
		}
	}

	@Override
	public boolean retornoGarantido() throws ExpressaoException {
		if (isEmpty()) {
			throw new ExpressaoException("erro.instrucoes.vazio");
		}
		return incondicional && getUltimo().retornoGarantido();
	}

	public LocalContexto getLocalContexto(String nome) {
		for (Contexto item : componentes) {
			if (item instanceof LocalContexto && item.getToken().getString().equals(nome)) {
				return (LocalContexto) item;
			}
		}
		return null;
	}
}