package br.com.persist.plugins.checagem.util;

import java.util.Collection;
import java.util.Map;

import br.com.persist.plugins.checagem.ChecagemException;
import br.com.persist.plugins.checagem.Contexto;
import br.com.persist.plugins.checagem.FuncaoUnaria;

public class IsEmpty extends FuncaoUnaria {
	@Override
	public Object executar(String key, Contexto ctx) throws ChecagemException {
		Object pri = param0().executar(key, ctx);
		if (pri instanceof Collection<?>) {
			return ((Collection<?>) pri).isEmpty();
		} else if (pri instanceof Map<?, ?>) {
			return ((Map<?, ?>) pri).isEmpty();
		}
		return pri == null || pri.toString().trim().length() == 0;
	}
}