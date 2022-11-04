package br.com.persist.plugins.checagem;

import java.math.BigDecimal;
import java.math.BigInteger;

public interface ChecagemNumero {
	public default boolean ehInteiro(Object obj) {
		return obj instanceof Byte || obj instanceof Short || obj instanceof Integer || obj instanceof Long;
	}

	public default boolean ehString(Object obj) {
		return obj instanceof String || obj instanceof Character;
	}

	public default boolean ehFlutuante(Object obj) {
		return obj instanceof Float || obj instanceof Double;
	}

	public default boolean ehBigInteger(Object obj) {
		return obj instanceof BigInteger;
	}

	public default boolean ehBigDecimal(Object obj) {
		return obj instanceof BigDecimal;
	}

	public default boolean ehBoolean(Object obj) {
		return obj instanceof Boolean;
	}

	public default long getNativoInteiro(Number number) {
		return number.longValue();
	}

	public default long getNativoInteiro(Object obj) {
		return getNativoInteiro((Number) obj);
	}

	public default double getNativoFlutuante(Number number) {
		return number.doubleValue();
	}

	public default double getNativoFlutuante(Object obj) {
		return getNativoFlutuante((Number) obj);
	}

	public default boolean getNativoBoolean(Object obj) {
		return ((Boolean) obj).booleanValue();
	}

	public default String getNativoString(Object obj) {
		return (String) obj;
	}

	public default BigInteger getNativoBigInteger(Object obj) {
		return (BigInteger) obj;
	}

	public default BigDecimal getNativoBigDecimal(Object obj) {
		return (BigDecimal) obj;
	}

	public default BigInteger criarBigInteger(long valor) {
		return BigInteger.valueOf(valor);
	}

	public default BigDecimal criarBigDecimal(BigInteger bigInteger) {
		return new BigDecimal(bigInteger);
	}

	public default BigDecimal criarBigDecimal(double valor) {
		return BigDecimal.valueOf(valor);
	}

	public static boolean ehFlutuanteInteiro(Object obj) {
		if (obj instanceof Double) {
			return (Double.doubleToLongBits((Double) obj) & 0x000fffffffffffffL) != 0;
		} else if (obj instanceof Float) {
			return (Float.floatToIntBits((Float) obj) & 0x007fffff) != 0;
		}
		return false;
	}
}