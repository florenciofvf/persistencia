package br.com.persist.plugins.atributo;

import java.util.Objects;

public class Metodo {
	private final String nome;

	public Metodo(String nome) {
		this.nome = Objects.requireNonNull(nome);
	}

	public String getNome() {
		return nome;
	}

	public boolean isConstrutor(String classe) {
		return nome.equals(classe);
	}

	public boolean isGet() {
		return nome.startsWith("get");
	}

	public boolean isSet() {
		return nome.startsWith("set");
	}

	public boolean isIs() {
		return nome.startsWith("is");
	}

	@Override
	public String toString() {
		return nome;
	}
}