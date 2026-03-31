package br.com.persist.plugins.expressao.compl;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.compl.biblio.ConstanteContexto;
import br.com.persist.plugins.expressao.compl.funcao.FuncaoContexto;
import br.com.persist.plugins.expressao.compl.funcao.ParametrosContexto;
import br.com.persist.plugins.expressao.compl.funcao.RetornoContexto;

public class FuncaoConstantesContexto extends FuncaoContexto {
	private RetornoContexto retornoContexto = new RetornoContexto();
	public static final String NOME_FUNCAO_CONSTANTES = "$constantes";

	public FuncaoConstantesContexto(Token token) {
		this.token = token;
		retornoVoid = true;
		ParametrosContexto parametros = new ParametrosContexto();
		componentes.add(parametros);
		parametros.parent = this;
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