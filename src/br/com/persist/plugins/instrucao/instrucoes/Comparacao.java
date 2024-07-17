package br.com.persist.plugins.instrucao.inst;

import java.math.BigDecimal;
import java.math.BigInteger;

import br.com.persist.plugins.instrucao.pro.Metodo;

public abstract class Comparacao extends Logico {
	protected Comparacao(Metodo metodo, String nome) {
		super(metodo, nome);
	}

	protected BigInteger igual(BigInteger e, BigInteger d) {
		return e.compareTo(d) == 0 ? createTrue() : createFalse();
	}

	protected BigInteger igual(BigDecimal e, BigDecimal d) {
		return e.compareTo(d) == 0 ? createTrue() : createFalse();
	}

	protected BigInteger equals(Object e, Object d) {
		return e.equals(d) ? createTrue() : createFalse();
	}

	protected BigInteger diferente(BigInteger e, BigInteger d) {
		return e.compareTo(d) != 0 ? createTrue() : createFalse();
	}

	protected BigInteger diferente(BigDecimal e, BigDecimal d) {
		return e.compareTo(d) != 0 ? createTrue() : createFalse();
	}

	protected BigInteger differ(Object e, Object d) {
		return !e.equals(d) ? createTrue() : createFalse();
	}

	protected BigInteger maior(BigInteger e, BigInteger d) {
		return e.compareTo(d) > 0 ? createTrue() : createFalse();
	}

	protected BigInteger maior(BigDecimal e, BigDecimal d) {
		return e.compareTo(d) > 0 ? createTrue() : createFalse();
	}

	protected BigInteger menor(BigInteger e, BigInteger d) {
		return e.compareTo(d) < 0 ? createTrue() : createFalse();
	}

	protected BigInteger menor(BigDecimal e, BigDecimal d) {
		return e.compareTo(d) < 0 ? createTrue() : createFalse();
	}

	protected BigInteger maiorI(BigInteger e, BigInteger d) {
		return e.compareTo(d) >= 0 ? createTrue() : createFalse();
	}

	protected BigInteger maiorI(BigDecimal e, BigDecimal d) {
		return e.compareTo(d) >= 0 ? createTrue() : createFalse();
	}

	protected BigInteger menorI(BigInteger e, BigInteger d) {
		return e.compareTo(d) <= 0 ? createTrue() : createFalse();
	}

	protected BigInteger menorI(BigDecimal e, BigDecimal d) {
		return e.compareTo(d) <= 0 ? createTrue() : createFalse();
	}
}