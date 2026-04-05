package br.com.persist.plugins.expressao.funcao;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.biblioteca.ConstanteContexto;
import br.com.persist.plugins.expressao.compilador.Contexto;
import br.com.persist.plugins.expressao.compilador.Token;

public class FuncaoConstantesContexto extends FuncaoContexto {
	private RetornoContexto retornoContexto = new RetornoContexto();
	public static final String NOME_FUNCAO_CONSTANTES = "$constantes";

	public FuncaoConstantesContexto(Token token) {
		adicionar2(new ParametrosContexto());
		this.token = token;
		retornoVoid = true;
	}

	@Override
	public void adicionar(Contexto c) throws ExpressaoException {
		if (c instanceof ConstanteContexto) {
			remove(retornoContexto);
			super.adicionar(c);
			super.adicionar(retornoContexto);
		} else {
			throw new ExpressaoException("erro.inclusao.funcao_constantes");
		}
	}
}