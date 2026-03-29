package br.com.persist.plugins.expressao.compilador;

import br.com.persist.plugins.expressao.ExpressaoException;

public class FuncaoConstantesContexto extends FuncaoContexto {
	private RetornoContexto retornoContexto = new RetornoContexto();

	public FuncaoConstantesContexto(Token token) {
		this.token = token;
	}

	@Override
	public void add(Contexto c) throws ExpressaoException {
		if (c instanceof ConstanteContexto) {
			remove(retornoContexto);
			super.add(c);
			add(retornoContexto);
		} else {
			throw new ExpressaoException("erro.inclusao.funcao_constantes");
		}
	}
}