package br.com.persist.plugins.mapa;

import br.com.persist.assistencia.Util;

public class Atributo {
	private final String nome;
	private final String valor;

	public Atributo(String nome, String valor) {
		if (Util.estaVazio(nome)) {
			throw new IllegalArgumentException("Nome do atributo vazio.");
		}
		if (Util.estaVazio(valor)) {
			throw new IllegalArgumentException("Valor do atributo vazio.");
		}
		this.nome = nome;
		this.valor = valor;
	}

	public String getNome() {
		return nome;
	}

	public String getValor() {
		return valor;
	}

	public Integer getValorInt() {
		return Integer.parseInt(valor);
	}

	public String toString() {
		return nome + "=" + valor;
	}
}