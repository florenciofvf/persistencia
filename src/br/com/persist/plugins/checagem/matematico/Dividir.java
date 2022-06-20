package br.com.persist.plugins.checagem.matematico;

import br.com.persist.plugins.checagem.ChecagemException;
import br.com.persist.plugins.checagem.Contexto;

public class Dividir extends Matematico {
	private static final String ERRO = "Erro Dividir";

	@Override
	public Object executar(Contexto ctx) throws ChecagemException {
		Object pri = param0().executar(ctx);
		Object seg = param1().executar(ctx);
		if (isNumericoValido(pri) && isNumericoValido(seg)) {
			return dividir(pri, seg);
		}
		throw new ChecagemException(ERRO);
	}

	private Object dividir(Object pri, Object seg) throws ChecagemException {
		if (pri instanceof Short) {
			return priShort(pri, seg);
		} else if (pri instanceof Integer) {
			return priInteger(pri, seg);
		} else if (pri instanceof Long) {
			return priLong(pri, seg);
		} else if (pri instanceof Float) {
			return priFloat(pri, seg);
		} else if (pri instanceof Double) {
			return priDouble(pri, seg);
		}
		throw new ChecagemException(ERRO);
	}

	private Object priShort(Object pri, Object seg) throws ChecagemException {
		if (seg instanceof Short) {
			check(getShort(seg));
			return getShort(pri) / getShort(seg);
		} else if (seg instanceof Integer) {
			check(getInteger(seg));
			return getShort(pri) / getInteger(seg);
		} else if (seg instanceof Long) {
			check(getLong(seg));
			return getShort(pri) / getLong(seg);
		} else if (seg instanceof Float) {
			check(getFloat(seg));
			return getShort(pri) / getFloat(seg);
		} else if (seg instanceof Double) {
			check(getDouble(seg));
			return getShort(pri) / getDouble(seg);
		}
		throw new ChecagemException(ERRO);
	}

	private Object priInteger(Object pri, Object seg) throws ChecagemException {
		if (seg instanceof Short) {
			check(getShort(seg));
			return getInteger(pri) / getShort(seg);
		} else if (seg instanceof Integer) {
			check(getInteger(seg));
			return getInteger(pri) / getInteger(seg);
		} else if (seg instanceof Long) {
			check(getLong(seg));
			return getInteger(pri) / getLong(seg);
		} else if (seg instanceof Float) {
			check(getFloat(seg));
			return getInteger(pri) / getFloat(seg);
		} else if (seg instanceof Double) {
			check(getDouble(seg));
			return getInteger(pri) / getDouble(seg);
		}
		throw new ChecagemException(ERRO);
	}

	private Object priLong(Object pri, Object seg) throws ChecagemException {
		if (seg instanceof Short) {
			check(getShort(seg));
			return getLong(pri) / getShort(seg);
		} else if (seg instanceof Integer) {
			check(getInteger(seg));
			return getLong(pri) / getInteger(seg);
		} else if (seg instanceof Long) {
			check(getLong(seg));
			return getLong(pri) / getLong(seg);
		} else if (seg instanceof Float) {
			check(getFloat(seg));
			return getLong(pri) / getFloat(seg);
		} else if (seg instanceof Double) {
			check(getDouble(seg));
			return getLong(pri) / getDouble(seg);
		}
		throw new ChecagemException(ERRO);
	}

	private Object priFloat(Object pri, Object seg) throws ChecagemException {
		if (seg instanceof Short) {
			check(getShort(seg));
			return getFloat(pri) / getShort(seg);
		} else if (seg instanceof Integer) {
			check(getInteger(seg));
			return getFloat(pri) / getInteger(seg);
		} else if (seg instanceof Long) {
			check(getLong(seg));
			return getFloat(pri) / getLong(seg);
		} else if (seg instanceof Float) {
			check(getFloat(seg));
			return getFloat(pri) / getFloat(seg);
		} else if (seg instanceof Double) {
			check(getDouble(seg));
			return getFloat(pri) / getDouble(seg);
		}
		throw new ChecagemException(ERRO);
	}

	private Object priDouble(Object pri, Object seg) throws ChecagemException {
		if (seg instanceof Short) {
			check(getShort(seg));
			return getDouble(pri) / getShort(seg);
		} else if (seg instanceof Integer) {
			check(getInteger(seg));
			return getDouble(pri) / getInteger(seg);
		} else if (seg instanceof Long) {
			check(getLong(seg));
			return getDouble(pri) / getLong(seg);
		} else if (seg instanceof Float) {
			check(getFloat(seg));
			return getDouble(pri) / getFloat(seg);
		} else if (seg instanceof Double) {
			check(getDouble(seg));
			return getDouble(pri) / getDouble(seg);
		}
		throw new ChecagemException(ERRO);
	}

	private void check(Number number) throws ChecagemException {
		if (number.longValue() == 0 || number.doubleValue() == 0) {
			throw new ChecagemException("Nao existe divisao por zero");
		}
	}
}