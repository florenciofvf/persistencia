package br.com.persist.plugins.instrucao.compilador;

public class ParamContexto extends Container {
	private final String nome;

	public ParamContexto(String nome) {
		this.nome = nome;
	}

	public String getNome() {
		return nome;
	}
}