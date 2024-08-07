package br.com.persist.plugins.instrucao.biblionativo;

import java.math.BigInteger;

public class System {
	private System() {
	}

	@Biblio
	public static BigInteger timeMillis() {
		try {
			return BigInteger.valueOf(java.lang.System.currentTimeMillis());
		} catch (Exception ex) {
			throw new IllegalStateException(ex);
		}
	}

	@Biblio
	public static BigInteger nanoTime() {
		try {
			return BigInteger.valueOf(java.lang.System.nanoTime());
		} catch (Exception ex) {
			throw new IllegalStateException(ex);
		}
	}
}