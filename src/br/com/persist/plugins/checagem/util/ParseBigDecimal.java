package br.com.persist.plugins.checagem.util;

import java.math.BigDecimal;

import br.com.persist.plugins.checagem.Bloco;
import br.com.persist.plugins.checagem.Checagem;
import br.com.persist.plugins.checagem.ChecagemException;
import br.com.persist.plugins.checagem.Contexto;
import br.com.persist.plugins.checagem.funcao.FuncaoUnaria;

public class ParseBigDecimal extends FuncaoUnaria {
	@Override
	public Object executar(Checagem checagem, Bloco bloco, Contexto ctx) throws ChecagemException {
		Object pri = param0().executar(checagem, bloco, ctx);
		if (pri != null) {
			String string = String.valueOf(pri);
			try {
				return new BigDecimal(string);
			} catch (Exception e) {
				throw new ChecagemException(getClass(), e.getMessage());
			}
		}
		return null;
	}

	@Override
	public String getDoc() throws ChecagemException {
		return "parseBigDecimal(Texto) : Flutuante";
	}
}