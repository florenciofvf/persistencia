package br.com.persist.plugins.instrucao.compilador.expressao;

import br.com.persist.plugins.instrucao.compilador.Container;

public class NumeroContexto extends Container {
	private final String numero;

	public NumeroContexto(String numero) {
		this.numero = numero;
	}

	public String getNumero() {
		return numero;
	}
}