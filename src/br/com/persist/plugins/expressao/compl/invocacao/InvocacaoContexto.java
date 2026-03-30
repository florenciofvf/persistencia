package br.com.persist.plugins.expressao.compl.invocacao;

import java.util.List;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.compl.Compilador;
import br.com.persist.plugins.expressao.compl.Context;
import br.com.persist.plugins.expressao.compl.Contexto;
import br.com.persist.plugins.expressao.compl.Token;

public class InvocacaoContexto extends Contexto {
	protected final Token operador;

	public InvocacaoContexto(Token operador) {
		this.operador = operador;
	}

	@Context("operador")
	@Override
	public void processar(Compilador compilador, Token token) throws ExpressaoException {
		compilador.invalidar(token);
	}

	@Override
	protected void empilharLocalPos(List<Contexto> lista) {
		lista.add(this);
	}
}