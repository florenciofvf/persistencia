package br.com.persist.plugins.checagem.util;

import java.math.BigInteger;

import br.com.persist.plugins.checagem.ChecagemException;
import br.com.persist.plugins.checagem.Contexto;
import br.com.persist.plugins.checagem.FuncaoUnaria;

public class ParseBigInteger extends FuncaoUnaria {
	@Override
	public Object executar(String key, Contexto ctx) throws ChecagemException {
		Object pri = param0().executar(key, ctx);
		if (pri != null) {
			String string = String.valueOf(pri);
			try {
				return new BigInteger(string);
			} catch (Exception e) {
				throw new ChecagemException(e.getMessage());
			}
		}
		return null;
	}
}