package br.com.persist.plugins.checagem.matematico;

import br.com.persist.plugins.checagem.ChecagemException;
import br.com.persist.plugins.checagem.Contexto;

public class Dividir extends Matematico {
	private static final String ERRO = "Erro Dividir";

	@Override
	public Object executar(String key, Contexto ctx) throws ChecagemException {
		Object pri = param0().executar(key, ctx);
		Object seg = param1().executar(key, ctx);
		if (pri == null && seg == null) {
			throw new ChecagemException(ERRO);
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
		throw new ChecagemException(ERRO);
	}

	private Object processarInteiro(Object pri, Object seg) throws ChecagemException {
		if (ehInteiro(seg)) {
			checkOperandoDiv(getNativoInteiro(seg));
			return getNativoInteiro(pri) / getNativoInteiro(seg);
		} else if (ehFlutuante(seg)) {
			checkOperandoDiv(getNativoFlutuante(seg));
			return getNativoInteiro(pri) / getNativoFlutuante(seg);
		} else if (ehBigInteger(seg)) {
			checkOperandoDiv(getNativoBigInteger(seg));
			return criarBigInteger(getNativoInteiro(pri)).divide(getNativoBigInteger(seg));
		} else if (ehBigDecimal(seg)) {
			checkOperandoDiv(getNativoBigDecimal(seg));
			return criarBigDecimal(getNativoInteiro(pri)).divide(getNativoBigDecimal(seg));
		}
		throw new ChecagemException(ERRO);
	}

	private Object processarFlutuante(Object pri, Object seg) throws ChecagemException {
		if (ehInteiro(seg)) {
			checkOperandoDiv(getNativoInteiro(seg));
			return getNativoFlutuante(pri) / getNativoInteiro(seg);
		} else if (ehFlutuante(seg)) {
			checkOperandoDiv(getNativoFlutuante(seg));
			return getNativoFlutuante(pri) / getNativoFlutuante(seg);
		} else if (ehBigInteger(seg)) {
			checkOperandoDiv(getNativoBigInteger(seg));
			return criarBigDecimal(getNativoFlutuante(pri)).divide(criarBigDecimal(getNativoBigInteger(seg)));
		} else if (ehBigDecimal(seg)) {
			checkOperandoDiv(getNativoBigDecimal(seg));
			return criarBigDecimal(getNativoFlutuante(pri)).divide(getNativoBigDecimal(seg));
		}
		throw new ChecagemException(ERRO);
	}

	private Object processarBigInteger(Object pri, Object seg) throws ChecagemException {
		if (ehInteiro(seg)) {
			checkOperandoDiv(getNativoInteiro(seg));
			return getNativoBigInteger(pri).divide(criarBigInteger(getNativoInteiro(seg)));
		} else if (ehFlutuante(seg)) {
			checkOperandoDiv(getNativoFlutuante(seg));
			return criarBigDecimal(getNativoBigInteger(pri)).divide(criarBigDecimal(getNativoFlutuante(seg)));
		} else if (ehBigInteger(seg)) {
			checkOperandoDiv(getNativoBigInteger(seg));
			return getNativoBigInteger(pri).divide(getNativoBigInteger(seg));
		} else if (ehBigDecimal(seg)) {
			checkOperandoDiv(getNativoBigDecimal(seg));
			return criarBigDecimal(getNativoBigInteger(pri)).divide(getNativoBigDecimal(seg));
		}
		throw new ChecagemException(ERRO);
	}

	private Object processarBigDecimal(Object pri, Object seg) throws ChecagemException {
		if (ehInteiro(seg)) {
			checkOperandoDiv(getNativoInteiro(seg));
			return getNativoBigDecimal(pri).divide(criarBigDecimal(getNativoInteiro(seg)));
		} else if (ehFlutuante(seg)) {
			checkOperandoDiv(getNativoFlutuante(seg));
			return getNativoBigDecimal(pri).divide(criarBigDecimal(getNativoFlutuante(seg)));
		} else if (ehBigInteger(seg)) {
			checkOperandoDiv(getNativoBigInteger(seg));
			return getNativoBigDecimal(pri).divide(criarBigDecimal(getNativoBigInteger(seg)));
		} else if (ehBigDecimal(seg)) {
			checkOperandoDiv(getNativoBigDecimal(seg));
			return getNativoBigDecimal(pri).divide(getNativoBigDecimal(seg));
		}
		throw new ChecagemException(ERRO);
	}
}