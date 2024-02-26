package br.com.persist.plugins.propriedade;

import java.util.Objects;

public class Campo extends Container {
	private final String nome;
	private final String valor;

	public Campo(String nome, String valor) {
		this.nome = Objects.requireNonNull(nome);
		this.valor = Objects.requireNonNull(valor);
	}

	public String getNome() {
		return nome;
	}

	public String getValor() {
		return valor;
	}

	@Override
	public void adicionar(Container c) {
		throw new IllegalStateException();
	}

	@Override
	public String toString() {
		return "Campo [nome=" + nome + ", valor=" + valor + "]";
	}
}