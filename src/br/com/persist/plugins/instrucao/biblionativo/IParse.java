package br.com.persist.plugins.instrucao.biblionativo;

import java.math.BigDecimal;
import java.math.BigInteger;

public class IParse {
	private IParse() {
	}

	@Biblio(0)
	public static BigInteger bigInteger(Object object) {
		return new BigInteger(object.toString());
	}

	@Biblio(1)
	public static BigDecimal bigDecimal(Object object) {
		return new BigDecimal(object.toString());
	}
}