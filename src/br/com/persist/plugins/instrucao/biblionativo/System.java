package br.com.persist.plugins.instrucao.biblionativo;

import java.math.BigInteger;

public class System {
	private System() {
	}

	@Biblio
	public static BigInteger timeMillis() {
		return BigInteger.valueOf(java.lang.System.currentTimeMillis());
	}

	@Biblio
	public static BigInteger nanoTime() {
		return BigInteger.valueOf(java.lang.System.nanoTime());
	}
}