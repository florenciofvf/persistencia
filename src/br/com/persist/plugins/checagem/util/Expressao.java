package br.com.persist.plugins.checagem.util;

import java.math.BigDecimal;
import java.math.BigInteger;

import br.com.persist.plugins.checagem.Bloco;
import br.com.persist.plugins.checagem.Checagem;
import br.com.persist.plugins.checagem.ChecagemException;
import br.com.persist.plugins.checagem.ChecagemNumero;
import br.com.persist.plugins.checagem.Contexto;
import br.com.persist.plugins.checagem.funcao.FuncaoUnaria;

public class Expressao extends FuncaoUnaria implements ChecagemNumero {
	private final boolean negarExpressao;

	public Expressao(boolean negarExpressao) {
		this.negarExpressao = negarExpressao;
	}

	@Override
	public Object executar(Checagem checagem, Bloco bloco, Contexto ctx) throws ChecagemException {
		Object obj = param0().executar(checagem, bloco, ctx);
		if (negarExpressao) {
			if (ehInteiro(obj)) {
				return getNativoInteiro(obj) * -1;
			} else if (ehFlutuante(obj)) {
				return getNativoFlutuante(obj) * -1;
			} else if (ehBigInteger(obj)) {
				return getNativoBigInteger(obj).multiply(BigInteger.valueOf(-1));
			} else if (ehBigDecimal(obj)) {
				return getNativoBigDecimal(obj).multiply(BigDecimal.valueOf(-1));
			}
			throw new ChecagemException(getClass(), "O valor nao pode ser negado >>> " + obj);
		}
		return obj;
	}

	@Override
	public String getDoc() throws ChecagemException {
		return "() : Objeto";
	}
}