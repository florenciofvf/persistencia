package br.com.persist.plugins.instrucao.compilador.funcao;

import br.com.persist.plugins.instrucao.compilador.Container;

public class ParametroContexto extends Container {
	private final String nome;

	public ParametroContexto(String nome) {
		this.nome = nome;
	}

	public String getNome() {
		return nome;
	}
}