package br.com.persist.plugins.instrucao.inst;

import java.math.BigInteger;

import br.com.persist.plugins.instrucao.Instrucao;
import br.com.persist.plugins.instrucao.Metodo;

public abstract class Logico extends Instrucao {
	public Logico(Metodo metodo, String nome) {
		super(metodo, nome);
	}

	protected BigInteger createFalse() {
		return BigInteger.valueOf(0);
	}

	protected BigInteger createTrue() {
		return BigInteger.valueOf(1);
	}
}