package br.com.persist.plugins.expressao.compl;

import java.util.List;

import br.com.persist.plugins.expressao.ExpressaoException;

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