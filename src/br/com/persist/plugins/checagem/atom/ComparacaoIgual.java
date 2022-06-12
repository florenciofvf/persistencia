package br.com.persist.plugins.checagem.atom;

import br.com.persist.plugins.checagem.ChecagemException;
import br.com.persist.plugins.checagem.Contexto;

public class ComparacaoIgual extends FuncaoBinaria {
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
		return Boolean.FALSE;
	}

	private Object priLong(Object pri, Object seg) {
		if (seg instanceof Long) {
			return ((Long) pri).longValue() == ((Long) seg).longValue();
		} else if (seg instanceof Double) {
			return ((Long) pri).longValue() == ((Double) seg).doubleValue();
		} else if (seg instanceof Boolean) {
			return false;
		} else if (seg instanceof String) {
			return false;
		}
		return Boolean.FALSE;
	}

	private Object priDouble(Object pri, Object seg) {
		if (seg instanceof Long) {
			return ((Double) pri).doubleValue() == ((Long) seg).longValue();
		} else if (seg instanceof Double) {
			return ((Double) pri).doubleValue() == ((Double) seg).doubleValue();
		} else if (seg instanceof Boolean) {
			return false;
		} else if (seg instanceof String) {
			return false;
		}
		return Boolean.FALSE;
	}

	private Object priBoolean(Object pri, Object seg) {
		if (seg instanceof Long) {
			return false;
		} else if (seg instanceof Double) {
			return false;
		} else if (seg instanceof Boolean) {
			return ((Boolean) pri).booleanValue() == ((Boolean) seg).booleanValue();
		} else if (seg instanceof String) {
			return false;
		}
		return Boolean.FALSE;
	}

	private Object priString(Object pri, Object seg) {
		if (seg instanceof Long) {
			return false;
		} else if (seg instanceof Double) {
			return false;
		} else if (seg instanceof Boolean) {
			return false;
		} else if (seg instanceof String) {
			return pri.equals(seg);
		}
		return Boolean.FALSE;
	}
}