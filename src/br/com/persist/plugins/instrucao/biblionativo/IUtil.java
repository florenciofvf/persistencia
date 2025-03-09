package br.com.persist.plugins.instrucao.biblionativo;

import java.math.BigInteger;

public class IUtil {
	public static final BigInteger FALSE = BigInteger.valueOf(0);
	public static final BigInteger TRUE = BigInteger.valueOf(1);

	private IUtil() {
	}

	@Biblio(1)
	public static BigInteger getFalse() {
		return FALSE;
	}

	@Biblio(2)
	public static BigInteger getTrue() {
		return TRUE;
	}

	@Biblio(5)
	public static BigInteger isNull(Object object) {
		return object == null ? TRUE : FALSE;
	}

	@Biblio(3)
	public static BigInteger isNotNull(Object object) {
		return object != null ? TRUE : FALSE;
	}

	@Biblio(4)
	public static String toString(Object object) {
		return object == null ? "" : object.toString();
	}

	@Biblio(6)
	public static Object log(Object desc, Object object) {
		System.out.println(desc + " " + object);
		return object;
	}
}