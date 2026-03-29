package br.com.persist.plugins.expressao.compilador;

import java.util.List;

import br.com.persist.plugins.expressao.ExpressaoException;

public class ExpressaoContexto extends Contexto {
	private static final String ERRO_PILHA_LOCAL_VAZIO = "erro.pilhaLocal.vazio";
	private IFEqContexto ifEqContexto;
	private final boolean comSalto;

	public ExpressaoContexto(boolean comSalto) {
		super(null);
		this.comSalto = comSalto;
	}

	@Context("expressao")
	@Doc({ "(valor)", "(valor operador valor)", "(valor operador expressao)", "(expressao operador valor)",
			"(expressao operador expressao)" })
	@Override
	public void processar(Compilador compilador, Token token) throws ExpressaoException {
		if (token.isFechaParentese()) {
			compilador.setSelecionado(parent);
		} else if (token.isAbreParentese()) {
			if (getUltimo() instanceof ChaveContexto) {
				InvocacaoContexto invocacao = new InvocacaoContexto(excluirUltimo().token);
				compilador.setSelecionado(invocacao);
				add(invocacao);
			} else {
				ExpressaoContexto expressao = new ExpressaoContexto(false);
				compilador.setSelecionado(expressao);
				add(expressao);
			}
		} else if (token.isOperador()) {
			add(new OperadorContexto(token));
		} else if (token.isString()) {
			add(new StringContexto(token));
		} else if (token.isInteiro()) {
			add(new InteiroContexto(token));
		} else if (token.isFlutuante()) {
			add(new FlutuanteContexto(token));
		} else if (token.isChave() || token.isChave2()) {
			add(new ChaveContexto(token));
		} else {
			compilador.invalidar(token);
		}
	}

	@Override
	protected void configurarSaltosPos() throws ExpressaoException {
		if (!comSalto) {
			return;
		}
		if (parent instanceof IFContexto || parent instanceof WhileContexto) {
			checarVazioExpressao();
			Contexto seOUloop = parent;
			Contexto instrucoes = seOUloop.parent;
			if (!(instrucoes instanceof InstrucoesContexto)) {
				throw new ExpressaoException("erro.estrutura.sem_parent", seOUloop.getClass().getName());
			}
			Contexto contextoApos = instrucoes.getApos(seOUloop);
			if (contextoApos != null) {
				contextoApos.empilharLocalIni();
				List<Contexto> pilha = contextoApos.getPilhaLocal();
				if (pilha.isEmpty()) {
					throw new ExpressaoException(ERRO_PILHA_LOCAL_VAZIO);
				}
				ifEqContexto = new IFEqContexto();
				ifEqContexto.setDestino(pilha.get(0));
				add(ifEqContexto);
			} else {
				if (instrucoes.parent instanceof FuncaoContexto) {
					throw new ExpressaoException("erro.funcao.sem_retorno");
				}
				configurarSaltoAcima(instrucoes.parent);
			}
		} else {
			throw new ExpressaoException("erro.estrutura.expressao.invalida");
		}
	}

	private void configurarSaltoAcima(Contexto contexto) throws ExpressaoException {
		if (contexto == null) {
			throw new ExpressaoException("erro.objeto.contexto.nulo");
		}
		if (contexto instanceof IFContexto || contexto instanceof WhileContexto) {
			Contexto instrucoes = contexto.parent;
			if (!(instrucoes instanceof InstrucoesContexto)) {
				throw new ExpressaoException("erro.estrutura.sem_parent", contexto.getClass().getName());
			}
			Contexto contextoApos = instrucoes.getApos(contexto);
			if (contextoApos != null) {
				contextoApos.empilharLocalIni();
				List<Contexto> pilha = contextoApos.getPilhaLocal();
				if (pilha.isEmpty()) {
					throw new ExpressaoException(ERRO_PILHA_LOCAL_VAZIO);
				}
				ifEqContexto = new IFEqContexto();
				ifEqContexto.setDestino(pilha.get(0));
				add(ifEqContexto);
			} else {
				if (instrucoes.parent instanceof FuncaoContexto) {
					throw new ExpressaoException("erro.funcao.sem_retorno");
				}
				configurarSaltoAcima(instrucoes.parent);
			}
		} else {
			throw new ExpressaoException("erro.estrutura.expressao.invalida");
		}
	}
}