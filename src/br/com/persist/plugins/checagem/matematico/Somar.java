package br.com.persist.plugins.checagem.matematico;

import br.com.persist.plugins.checagem.Bloco;
import br.com.persist.plugins.checagem.Checagem;
import br.com.persist.plugins.checagem.ChecagemException;
import br.com.persist.plugins.checagem.Contexto;

public class Somar extends Matematico {
	@Override
	public Object executar(Checagem checagem, Bloco bloco, Contexto ctx) throws ChecagemException {
		Object pri = param0().executar(checagem, bloco, ctx);
		Object seg = param1().executar(checagem, bloco, ctx);
		if (ehInteiro(pri)) {
			return processarInteiro(pri, seg);
		} else if (ehFlutuante(pri)) {
			return processarFlutuante(pri, seg);
		} else if (ehBigInteger(pri)) {
			return processarBigInteger(pri, seg);
		} else if (ehBigDecimal(pri)) {
			return processarBigDecimal(pri, seg);
		}
		return concatenar(pri, seg);
	}

	private Object processarInteiro(Object pri, Object seg) {
		if (ehInteiro(seg)) {
			return getNativoInteiro(pri) + getNativoInteiro(seg);
		} else if (ehFlutuante(seg)) {
			return getNativoInteiro(pri) + getNativoFlutuante(seg);
		} else if (ehBigInteger(seg)) {
			return criarBigInteger(getNativoInteiro(pri)).add(getNativoBigInteger(seg));
		} else if (ehBigDecimal(seg)) {
			return criarBigDecimal(getNativoInteiro(pri)).add(getNativoBigDecimal(seg));
		}
		return concatenar(pri, seg);
	}

	private Object processarFlutuante(Object pri, Object seg) {
		if (ehInteiro(seg)) {
			return getNativoFlutuante(pri) + getNativoInteiro(seg);
		} else if (ehFlutuante(seg)) {
			return getNativoFlutuante(pri) + getNativoFlutuante(seg);
		} else if (ehBigInteger(seg)) {
			return criarBigDecimal(getNativoFlutuante(pri)).add(criarBigDecimal(getNativoBigInteger(seg)));
		} else if (ehBigDecimal(seg)) {
			return criarBigDecimal(getNativoFlutuante(pri)).add(getNativoBigDecimal(seg));
		}
		return concatenar(pri, seg);
	}

	private Object processarBigInteger(Object pri, Object seg) {
		if (ehInteiro(seg)) {
			return getNativoBigInteger(pri).add(criarBigInteger(getNativoInteiro(seg)));
		} else if (ehFlutuante(seg)) {
			return criarBigDecimal(getNativoBigInteger(pri)).add(criarBigDecimal(getNativoFlutuante(seg)));
		} else if (ehBigInteger(seg)) {
			return getNativoBigInteger(pri).add(getNativoBigInteger(seg));
		} else if (ehBigDecimal(seg)) {
			return criarBigDecimal(getNativoBigInteger(pri)).add(getNativoBigDecimal(seg));
		}
		return concatenar(pri, seg);
	}

	private Object processarBigDecimal(Object pri, Object seg) {
		if (ehInteiro(seg)) {
			return getNativoBigDecimal(pri).add(criarBigDecimal(getNativoInteiro(seg)));
		} else if (ehFlutuante(seg)) {
			return getNativoBigDecimal(pri).add(criarBigDecimal(getNativoFlutuante(seg)));
		} else if (ehBigInteger(seg)) {
			return getNativoBigDecimal(pri).add(criarBigDecimal(getNativoBigInteger(seg)));
		} else if (ehBigDecimal(seg)) {
			return getNativoBigDecimal(pri).add(getNativoBigDecimal(seg));
		}
		return concatenar(pri, seg);
	}

	@Override
	public short getNivel() {
		return matematico3;
	}

	@Override
	public String getDoc() throws ChecagemException {
		return "Objeto + Objeto";
	}
}