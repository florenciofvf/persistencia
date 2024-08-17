package br.com.persist.plugins.mapa;

import br.com.persist.assistencia.ArgumentoException;
import br.com.persist.assistencia.Util;

public class Atributo {
	private final String nome;
	private final String valor;

	public Atributo(String nome, String valor) throws ArgumentoException {
		if (Util.isEmpty(nome)) {
			throw new ArgumentoException("Nome do atributo vazio.");
		}
		if (Util.isEmpty(valor)) {
			throw new ArgumentoException("Valor do atributo vazio.");
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