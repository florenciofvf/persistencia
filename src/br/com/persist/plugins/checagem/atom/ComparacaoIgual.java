package br.com.persist.plugins.checagem.atom;

import br.com.persist.plugins.checagem.Contexto;
import br.com.persist.plugins.checagem.Controle;

public class ComparacaoIgual extends Controle {

	@Override
	public Object executar(Contexto ctx) {
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
			return ((Long) pri).longValue() == ((Long) seg).longValue();
		} else if (seg instanceof Double) {
			return ((Long) pri).longValue() == ((Double) seg).doubleValue();
		} else if (seg instanceof Boolean) {
			return false;
		} else if (seg instanceof String) {
			return false;
		}
		throw new IllegalStateException();
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
		throw new IllegalStateException();
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
		throw new IllegalStateException();
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
		throw new IllegalStateException();
	}
}