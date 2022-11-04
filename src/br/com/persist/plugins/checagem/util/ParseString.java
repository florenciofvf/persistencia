package br.com.persist.plugins.checagem.util;

import br.com.persist.plugins.checagem.Bloco;
import br.com.persist.plugins.checagem.Checagem;
import br.com.persist.plugins.checagem.ChecagemException;
import br.com.persist.plugins.checagem.Contexto;
import br.com.persist.plugins.checagem.funcao.FuncaoUnaria;

public class ParseString extends FuncaoUnaria {
	@Override
	public Object executar(Checagem checagem, Bloco bloco, Contexto ctx) throws ChecagemException {
		Object pri = param0().executar(checagem, bloco, ctx);
		if (pri != null) {
			return String.valueOf(pri);
		}
		return null;
	}

	@Override
	public String getDoc() throws ChecagemException {
		return "parseString(Objeto) : Texto";
	}
}