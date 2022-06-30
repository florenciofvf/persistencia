package br.com.persist.plugins.checagem.comparacao;

import br.com.persist.plugins.checagem.Bloco;
import br.com.persist.plugins.checagem.Checagem;
import br.com.persist.plugins.checagem.ChecagemException;
import br.com.persist.plugins.checagem.Contexto;

public class Igual extends Comparacao {

	@Override
	public Object executar(Checagem checagem, Bloco bloco, Contexto ctx) throws ChecagemException {
		Object pri = param0().executar(checagem, bloco, ctx);
		Object seg = param1().executar(checagem, bloco, ctx);
		if (pri == null && seg == null) {
			return Boolean.TRUE;
		}
		if (ehInteiro(pri)) {
			return processarInteiro(pri, seg);
		} else if (ehFlutuante(pri)) {
			return processarFlutuante(pri, seg);
		} else if (ehBigInteger(pri)) {
			return processarBigInteger(pri, seg);
		} else if (ehBigDecimal(pri)) {
			return processarBigDecimal(pri, seg);
		} else if (ehBoolean(pri)) {
			return processarBoolean(pri, seg);
		} else if (ehString(pri)) {
			return processarString(pri, seg);
		}
		return iguais(pri, seg);
	}

	private Object processarBoolean(Object pri, Object seg) {
		if (seg instanceof Boolean) {
			return getNativoBoolean(pri) == getNativoBoolean(seg);
		}
		return Boolean.FALSE;
	}

	private Object processarString(Object pri, Object seg) {
		if (seg instanceof String) {
			return getNativoString(pri).equalsIgnoreCase(getNativoString(seg));
		} else if (seg instanceof Character) {
			return getNativoString(pri).equalsIgnoreCase("" + seg);
		}
		return Boolean.FALSE;
	}

	private Object processarInteiro(Object pri, Object seg) {
		if (ehInteiro(seg)) {
			return getNativoInteiro(pri) == getNativoInteiro(seg);
		} else if (ehFlutuante(seg)) {
			return getNativoInteiro(pri) == getNativoFlutuante(seg);
		} else if (ehBigInteger(seg)) {
			return criarBigInteger(getNativoInteiro(pri)).equals(getNativoBigInteger(seg));
		} else if (ehBigDecimal(seg)) {
			return criarBigDecimal(getNativoInteiro(pri)).equals(getNativoBigDecimal(seg));
		}
		return Boolean.FALSE;
	}

	private Object processarFlutuante(Object pri, Object seg) {
		if (ehInteiro(seg)) {
			return getNativoFlutuante(pri) == getNativoInteiro(seg);
		} else if (ehFlutuante(seg)) {
			return getNativoFlutuante(pri) == getNativoFlutuante(seg);
		} else if (ehBigInteger(seg)) {
			return criarBigDecimal(getNativoFlutuante(pri)).equals(criarBigDecimal(getNativoBigInteger(seg)));
		} else if (ehBigDecimal(seg)) {
			return criarBigDecimal(getNativoFlutuante(pri)).equals(getNativoBigDecimal(seg));
		}
		return Boolean.FALSE;
	}

	private Object processarBigInteger(Object pri, Object seg) {
		if (ehInteiro(seg)) {
			return getNativoBigInteger(pri).equals(criarBigInteger(getNativoInteiro(seg)));
		} else if (ehFlutuante(seg)) {
			return criarBigDecimal(getNativoBigInteger(pri)).equals(criarBigDecimal(getNativoFlutuante(seg)));
		} else if (ehBigInteger(seg)) {
			return getNativoBigInteger(pri).equals(getNativoBigInteger(seg));
		} else if (ehBigDecimal(seg)) {
			return criarBigDecimal(getNativoBigInteger(pri)).equals(getNativoBigDecimal(seg));
		}
		return Boolean.FALSE;
	}

	private Object processarBigDecimal(Object pri, Object seg) {
		if (ehInteiro(seg)) {
			return getNativoBigDecimal(pri).equals(criarBigDecimal(getNativoInteiro(seg)));
		} else if (ehFlutuante(seg)) {
			return getNativoBigDecimal(pri).equals(criarBigDecimal(getNativoFlutuante(seg)));
		} else if (ehBigInteger(seg)) {
			return getNativoBigDecimal(pri).equals(criarBigDecimal(getNativoBigInteger(seg)));
		} else if (ehBigDecimal(seg)) {
			return getNativoBigDecimal(pri).equals(getNativoBigDecimal(seg));
		}
		return Boolean.FALSE;
	}
}