package br.com.persist.plugins.expressao.compl.funcao;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.compl.Contexto;
import br.com.persist.plugins.expressao.compl.Token;
import br.com.persist.plugins.expressao.compl.biblio.ConstanteContexto;

public class FuncaoConstantesContexto extends FuncaoContexto {
	private RetornoContexto retornoContexto = new RetornoContexto();
	public static final String NOME_FUNCAO_CONSTANTES = "$constantes";

	public FuncaoConstantesContexto(Token token) {
		add2(new ParametrosContexto());
		this.token = token;
		retornoVoid = true;
	}

	@Override
	public void add(Contexto c) throws ExpressaoException {
		if (c instanceof ConstanteContexto) {
			remove(retornoContexto);
			super.add(c);
			super.add(retornoContexto);
		} else {
			throw new ExpressaoException("erro.inclusao.funcao_constantes");
		}
	}
}