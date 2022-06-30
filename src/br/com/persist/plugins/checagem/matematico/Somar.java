package br.com.persist.plugins.checagem.matematico;

import br.com.persist.plugins.checagem.Bloco;
import br.com.persist.plugins.checagem.Checagem;
import br.com.persist.plugins.checagem.ChecagemException;
import br.com.persist.plugins.checagem.Contexto;

public class Somar extends Matematico {
	private static final String ERRO = "Erro Somar";

	@Override
	public Object executar(Checagem checagem, Bloco bloco, Contexto ctx) throws ChecagemException {
		Object pri = param0().executar(checagem, bloco, ctx);
		Object seg = param1().executar(checagem, bloco, ctx);
		if (pri == null && seg == null) {
			return "nullnull";
		}
		if (ehInteiro(pri)) {
			return processarInteiro(pri, seg);
		} else if (ehFlutuante(pri)) {
			return processarFlutuante(pri, seg);
		} else if (ehBigInteger(pri)) {
			return processarBigInteger(pri, seg);
		} else if (ehBigDecimal(pri)) {
			return processarBigDecimal(pri, seg);
		} else if (pri != null && seg != null) {
			return pri.toString() + seg.toString();
		}
		return concatenar(pri, seg);
	}

	private Object processarInteiro(Object pri, Object seg) throws ChecagemException {
		if (ehInteiro(seg)) {
			return getNativoInteiro(pri) + getNativoInteiro(seg);
		} else if (ehFlutuante(seg)) {
			return getNativoInteiro(pri) + getNativoFlutuante(seg);
		} else if (ehBigInteger(seg)) {
			return criarBigInteger(getNativoInteiro(pri)).add(getNativoBigInteger(seg));
		} else if (ehBigDecimal(seg)) {
			return criarBigDecimal(getNativoInteiro(pri)).add(getNativoBigDecimal(seg));
		}
		throw new ChecagemException(ERRO);
	}

	private Object processarFlutuante(Object pri, Object seg) throws ChecagemException {
		if (ehInteiro(seg)) {
			return getNativoFlutuante(pri) + getNativoInteiro(seg);
		} else if (ehFlutuante(seg)) {
			return getNativoFlutuante(pri) + getNativoFlutuante(seg);
		} else if (ehBigInteger(seg)) {
			return criarBigDecimal(getNativoFlutuante(pri)).add(criarBigDecimal(getNativoBigInteger(seg)));
		} else if (ehBigDecimal(seg)) {
			return criarBigDecimal(getNativoFlutuante(pri)).add(getNativoBigDecimal(seg));
		}
		throw new ChecagemException(ERRO);
	}

	private Object processarBigInteger(Object pri, Object seg) throws ChecagemException {
		if (ehInteiro(seg)) {
			return getNativoBigInteger(pri).add(criarBigInteger(getNativoInteiro(seg)));
		} else if (ehFlutuante(seg)) {
			return criarBigDecimal(getNativoBigInteger(pri)).add(criarBigDecimal(getNativoFlutuante(seg)));
		} else if (ehBigInteger(seg)) {
			return getNativoBigInteger(pri).add(getNativoBigInteger(seg));
		} else if (ehBigDecimal(seg)) {
			return criarBigDecimal(getNativoBigInteger(pri)).add(getNativoBigDecimal(seg));
		}
		throw new ChecagemException(ERRO);
	}

	private Object processarBigDecimal(Object pri, Object seg) throws ChecagemException {
		if (ehInteiro(seg)) {
			return getNativoBigDecimal(pri).add(criarBigDecimal(getNativoInteiro(seg)));
		} else if (ehFlutuante(seg)) {
			return getNativoBigDecimal(pri).add(criarBigDecimal(getNativoFlutuante(seg)));
		} else if (ehBigInteger(seg)) {
			return getNativoBigDecimal(pri).add(criarBigDecimal(getNativoBigInteger(seg)));
		} else if (ehBigDecimal(seg)) {
			return getNativoBigDecimal(pri).add(getNativoBigDecimal(seg));
		}
		throw new ChecagemException(ERRO);
	}
}