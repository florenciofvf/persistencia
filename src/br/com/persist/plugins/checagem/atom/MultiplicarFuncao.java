package br.com.persist.plugins.checagem.atom;

import br.com.persist.plugins.checagem.ChecagemException;
import br.com.persist.plugins.checagem.Contexto;

public class MultiplicarFuncao extends FuncaoBinaria {
	private static final String ERRO = "Erro multiplicacao";

	@Override
	public Object executar(Contexto ctx) throws ChecagemException {
		Object pri = param0().executar(ctx);
		Object seg = param1().executar(ctx);
		if (pri instanceof Long) {
			return priLong(pri, seg);
		} else if (pri instanceof Double) {
			return priDouble(pri, seg);
		} else if (pri instanceof Boolean) {
			return priBoolean(pri, seg);
		} else if (pri instanceof String) {
			return priString(pri, seg);
		}
		throw new ChecagemException(ERRO);
	}

	private Object priLong(Object pri, Object seg) throws ChecagemException {
		if (seg instanceof Long) {
			return ((Long) pri).longValue() * ((Long) seg).longValue();
		} else if (seg instanceof Double) {
			return ((Long) pri).longValue() * ((Double) seg).doubleValue();
		}
		throw new ChecagemException(ERRO);
	}

	private Object priDouble(Object pri, Object seg) throws ChecagemException {
		if (seg instanceof Long) {
			return ((Double) pri).doubleValue() * ((Long) seg).longValue();
		} else if (seg instanceof Double) {
			return ((Double) pri).doubleValue() * ((Double) seg).doubleValue();
		}
		throw new ChecagemException(ERRO);
	}

	private Object priBoolean(Object pri, Object seg) throws ChecagemException {
		throw new ChecagemException(ERRO);
	}

	private Object priString(Object pri, Object seg) throws ChecagemException {
		throw new ChecagemException(ERRO);
	}
}