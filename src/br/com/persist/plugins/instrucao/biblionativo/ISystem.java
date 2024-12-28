package br.com.persist.plugins.instrucao.biblionativo;

import java.math.BigInteger;

public class ISystem {
	private ISystem() {
	}

	@Biblio(0)
	public static BigInteger timeMillis() {
		return BigInteger.valueOf(System.currentTimeMillis());
	}

	@Biblio(1)
	public static BigInteger nanoTime() {
		return BigInteger.valueOf(System.nanoTime());
	}
}