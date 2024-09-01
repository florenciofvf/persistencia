package br.com.persist.plugins.instrucao.biblionativo;

import java.math.BigInteger;

public class ISystem {
	private ISystem() {
	}

	@Biblio
	public static BigInteger timeMillis() {
		return BigInteger.valueOf(System.currentTimeMillis());
	}

	@Biblio
	public static BigInteger nanoTime() {
		return BigInteger.valueOf(System.nanoTime());
	}
}