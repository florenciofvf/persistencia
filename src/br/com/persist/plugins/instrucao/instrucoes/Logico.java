package br.com.persist.plugins.instrucao.inst;

import java.math.BigInteger;

import br.com.persist.plugins.instrucao.pro.Metodo;

public abstract class Logico extends Matemat {
	protected Logico(Metodo metodo, String nome) {
		super(metodo, nome);
	}

	protected BigInteger createFalse() {
		return BigInteger.valueOf(0);
	}

	protected BigInteger createTrue() {
		return BigInteger.valueOf(1);
	}
}