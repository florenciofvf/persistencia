package br.com.persist.plugins.checagem.matematico;

import br.com.persist.plugins.checagem.ChecagemException;
import br.com.persist.plugins.checagem.Contexto;

public class Somar extends Matematico {
	private static final String ERRO = "Erro Somar";

	@Override
	public Object executar(Contexto ctx) throws ChecagemException {
		Object pri = param0().executar(ctx);
		Object seg = param1().executar(ctx);
		if (isNumericoValido(pri) && isNumericoValido(seg)) {
			return somar(pri, seg);
		} else if (pri != null && seg != null) {
			return pri.toString() + seg.toString();
		}
		return concatenar(pri, seg);
	}

	private Object concatenar(Object pri, Object seg) {
		if (pri != null && seg == null) {
			return pri.toString() + "null";
		} else if (pri == null && seg != null) {
			return "null" + seg.toString();
		}
		return "nullnull";
	}

	private Object somar(Object pri, Object seg) throws ChecagemException {
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
			return getShort(pri) + getShort(seg);
		} else if (seg instanceof Integer) {
			return getShort(pri) + getInteger(seg);
		} else if (seg instanceof Long) {
			return getShort(pri) + getLong(seg);
		} else if (seg instanceof Float) {
			return getShort(pri) + getFloat(seg);
		} else if (seg instanceof Double) {
			return getShort(pri) + getDouble(seg);
		}
		throw new ChecagemException(ERRO);
	}

	private Object priInteger(Object pri, Object seg) throws ChecagemException {
		if (seg instanceof Short) {
			return getInteger(pri) + getShort(seg);
		} else if (seg instanceof Integer) {
			return getInteger(pri) + getInteger(seg);
		} else if (seg instanceof Long) {
			return getInteger(pri) + getLong(seg);
		} else if (seg instanceof Float) {
			return getInteger(pri) + getFloat(seg);
		} else if (seg instanceof Double) {
			return getInteger(pri) + getDouble(seg);
		}
		throw new ChecagemException(ERRO);
	}

	private Object priLong(Object pri, Object seg) throws ChecagemException {
		if (seg instanceof Short) {
			return getLong(pri) + getShort(seg);
		} else if (seg instanceof Integer) {
			return getLong(pri) + getInteger(seg);
		} else if (seg instanceof Long) {
			return getLong(pri) + getLong(seg);
		} else if (seg instanceof Float) {
			return getLong(pri) + getFloat(seg);
		} else if (seg instanceof Double) {
			return getLong(pri) + getDouble(seg);
		}
		throw new ChecagemException(ERRO);
	}

	private Object priFloat(Object pri, Object seg) throws ChecagemException {
		if (seg instanceof Short) {
			return getFloat(pri) + getShort(seg);
		} else if (seg instanceof Integer) {
			return getFloat(pri) + getInteger(seg);
		} else if (seg instanceof Long) {
			return getFloat(pri) + getLong(seg);
		} else if (seg instanceof Float) {
			return getFloat(pri) + getFloat(seg);
		} else if (seg instanceof Double) {
			return getFloat(pri) + getDouble(seg);
		}
		throw new ChecagemException(ERRO);
	}

	private Object priDouble(Object pri, Object seg) throws ChecagemException {
		if (seg instanceof Short) {
			return getDouble(pri) + getShort(seg);
		} else if (seg instanceof Integer) {
			return getDouble(pri) + getInteger(seg);
		} else if (seg instanceof Long) {
			return getDouble(pri) + getLong(seg);
		} else if (seg instanceof Float) {
			return getDouble(pri) + getFloat(seg);
		} else if (seg instanceof Double) {
			return getDouble(pri) + getDouble(seg);
		}
		throw new ChecagemException(ERRO);
	}
}