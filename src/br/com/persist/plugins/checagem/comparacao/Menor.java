package br.com.persist.plugins.checagem.comparacao;

import br.com.persist.plugins.checagem.Bloco;
import br.com.persist.plugins.checagem.Checagem;
import br.com.persist.plugins.checagem.ChecagemException;
import br.com.persist.plugins.checagem.Contexto;

public class Menor extends Comparacao {
	private static final String ERRO = "Erro Menor";

	@Override
	public Object executar(Checagem checagem, Bloco bloco, Contexto ctx) throws ChecagemException {
		Object pri = param0().executar(checagem, bloco, ctx);
		Object seg = param1().executar(checagem, bloco, ctx);
		if (pri == null && seg == null) {
			throw new ChecagemException(getClass(), ERRO);
		}
		if (ehInteiro(pri)) {
			return processarInteiro(pri, seg);
		} else if (ehFlutuante(pri)) {
			return processarFlutuante(pri, seg);
		} else if (ehBigInteger(pri)) {
			return processarBigInteger(pri, seg);
		} else if (ehBigDecimal(pri)) {
			return processarBigDecimal(pri, seg);
		}
		throw new ChecagemException(getClass(), ERRO);
	}

	private Object processarInteiro(Object pri, Object seg) throws ChecagemException {
		if (ehInteiro(seg)) {
			return getNativoInteiro(pri) < getNativoInteiro(seg);
		} else if (ehFlutuante(seg)) {
			return getNativoInteiro(pri) < getNativoFlutuante(seg);
		} else if (ehBigInteger(seg)) {
			return criarBigInteger(getNativoInteiro(pri)).compareTo(getNativoBigInteger(seg)) < 0;
		} else if (ehBigDecimal(seg)) {
			return criarBigDecimal(getNativoInteiro(pri)).compareTo(getNativoBigDecimal(seg)) < 0;
		}
		throw new ChecagemException(getClass(), ERRO);
	}

	private Object processarFlutuante(Object pri, Object seg) throws ChecagemException {
		if (ehInteiro(seg)) {
			return getNativoFlutuante(pri) < getNativoInteiro(seg);
		} else if (ehFlutuante(seg)) {
			return getNativoFlutuante(pri) < getNativoFlutuante(seg);
		} else if (ehBigInteger(seg)) {
			return criarBigDecimal(getNativoFlutuante(pri)).compareTo(criarBigDecimal(getNativoBigInteger(seg))) < 0;
		} else if (ehBigDecimal(seg)) {
			return criarBigDecimal(getNativoFlutuante(pri)).compareTo(getNativoBigDecimal(seg)) < 0;
		}
		throw new ChecagemException(getClass(), ERRO);
	}

	private Object processarBigInteger(Object pri, Object seg) throws ChecagemException {
		if (ehInteiro(seg)) {
			return getNativoBigInteger(pri).compareTo(criarBigInteger(getNativoInteiro(seg))) < 0;
		} else if (ehFlutuante(seg)) {
			return criarBigDecimal(getNativoBigInteger(pri)).compareTo(criarBigDecimal(getNativoFlutuante(seg))) < 0;
		} else if (ehBigInteger(seg)) {
			return getNativoBigInteger(pri).compareTo(getNativoBigInteger(seg)) < 0;
		} else if (ehBigDecimal(seg)) {
			return criarBigDecimal(getNativoBigInteger(pri)).compareTo(getNativoBigDecimal(seg)) < 0;
		}
		throw new ChecagemException(getClass(), ERRO);
	}

	private Object processarBigDecimal(Object pri, Object seg) throws ChecagemException {
		if (ehInteiro(seg)) {
			return getNativoBigDecimal(pri).compareTo(criarBigDecimal(getNativoInteiro(seg))) < 0;
		} else if (ehFlutuante(seg)) {
			return getNativoBigDecimal(pri).compareTo(criarBigDecimal(getNativoFlutuante(seg))) < 0;
		} else if (ehBigInteger(seg)) {
			return getNativoBigDecimal(pri).compareTo(criarBigDecimal(getNativoBigInteger(seg))) < 0;
		} else if (ehBigDecimal(seg)) {
			return getNativoBigDecimal(pri).compareTo(getNativoBigDecimal(seg)) < 0;
		}
		throw new ChecagemException(getClass(), ERRO);
	}

	@Override
	public short getNivel() {
		return comparacao1;
	}

	@Override
	public String getDoc() throws ChecagemException {
		return "Numero < Numero";
	}
}