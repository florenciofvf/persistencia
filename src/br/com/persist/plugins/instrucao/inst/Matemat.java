package br.com.persist.plugins.instrucao.inst;

import java.math.BigDecimal;
import java.math.BigInteger;

import br.com.persist.plugins.instrucao.Instrucao;
import br.com.persist.plugins.instrucao.Metodo;

public abstract class Matemat extends Instrucao {
	public Matemat(Metodo metodo, String nome) {
		super(metodo, nome);
	}

	protected BigInteger castBI(Object obj) {
		return (BigInteger) obj;
	}

	protected BigDecimal castBD(Object obj) {
		return (BigDecimal) obj;
	}

	protected BigDecimal createBD(BigInteger bigInteger) {
		return new BigDecimal(bigInteger);
	}
}