package br.com.persist.plugins.expressao.processador;

import java.util.Objects;

public class Constante {
	private Biblioteca biblioteca;
	private final String nome;
	private Object valor;

	public Constante(String nome) {
		this.nome = Objects.requireNonNull(nome);
	}

	public Biblioteca getBiblioteca() {
		return biblioteca;
	}

	public void setBiblioteca(Biblioteca biblioteca) {
		this.biblioteca = biblioteca;
	}

	public String getNome() {
		return nome;
	}

	public Object getValor() {
		return valor;
	}

	public void setValor(Object valor) {
		this.valor = valor;
	}

	@Override
	public String toString() {
		return nome + ": " + valor;
	}
}