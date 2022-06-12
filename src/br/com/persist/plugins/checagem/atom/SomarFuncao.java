package br.com.persist.plugins.checagem.atom;

import br.com.persist.plugins.checagem.ChecagemException;
import br.com.persist.plugins.checagem.Contexto;

public class SomarFuncao extends FuncaoBinaria {
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
		throw new IllegalStateException();
	}

	private Object priLong(Object pri, Object seg) {
		if (seg instanceof Long) {
			return ((Long) pri).longValue() + ((Long) seg).longValue();
		} else if (seg instanceof Double) {
			return ((Long) pri).longValue() + ((Double) seg).doubleValue();
		} else if (seg instanceof String) {
			return pri.toString() + seg.toString();
		}
		throw new IllegalStateException();
	}

	private Object priDouble(Object pri, Object seg) {
		if (seg instanceof Long) {
			return ((Double) pri).doubleValue() + ((Long) seg).longValue();
		} else if (seg instanceof Double) {
			return ((Double) pri).doubleValue() + ((Double) seg).doubleValue();
		} else if (seg instanceof String) {
			return pri.toString() + seg.toString();
		}
		throw new IllegalStateException();
	}

	private Object priBoolean(Object pri, Object seg) {
		if (seg instanceof String) {
			return pri.toString() + seg.toString();
		}
		throw new IllegalStateException();
	}

	private Object priString(Object pri, Object seg) {
		if (seg instanceof Long || seg instanceof Double || seg instanceof Boolean || seg instanceof String) {
			return pri.toString() + seg.toString();
		}
		throw new IllegalStateException();
	}
}