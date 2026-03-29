package br.com.persist.plugins.expressao.compilador;

import br.com.persist.plugins.expressao.ExpressaoConstantes;

public class InstrucoesContexto extends Contexto {
	public static final byte FUNCAO = 0;
	public static final byte LOOP = 1;
	public static final byte SE = 2;
	private final byte estrutura;

	public InstrucoesContexto(byte estrutura) {
		super();
		this.estrutura = estrutura;
	}

	public InstrucoesContexto() {
		this(FUNCAO);
	}

	@Override
	public void processar(Compilador compilador, Token token) {
		if (token.isReservado()) {
			if (ExpressaoConstantes.CONST.equals(token.getString())) {
				ConstanteContexto constante = new ConstanteContexto();
				compilador.setSelecionado(constante);
				add(constante);
			} else if (ExpressaoConstantes.RETURN.equals(token.getString())) {
				RetornoContexto retorno = new RetornoContexto();
				compilador.setSelecionado(retorno);
				add(retorno);
			} else if (ExpressaoConstantes.IF.equals(token.getString())) {
				IFContexto se = new IFContexto();
				compilador.setSelecionado(se);
				add(se);
			} else if (ExpressaoConstantes.WHILE.equals(token.getString())) {
				WhileContexto loop = new WhileContexto();
				compilador.setSelecionado(loop);
				add(loop);
			} else {
				compilador.invalidar(token);
			}
		} else if (token.isChave() || token.isChave2()) {
			InvocacaoContexto invocacao = new InvocacaoContexto(token);
			compilador.setSelecionado(invocacao);
			add(invocacao);
		} else {
			compilador.invalidar(token);
		}
	}
}