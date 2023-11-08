package br.com.persist.plugins.persistencia;

import java.util.Objects;

public class ChaveValor {
	private final String chave;
	private Object valor;

	public ChaveValor(String chave, Object valor) {
		this.chave = Objects.requireNonNull(chave);
		this.valor = valor;
	}

	public Object getValor() {
		return valor;
	}

	public void setValor(Object valor) {
		this.valor = valor;
	}

	public String getChave() {
		return chave;
	}
}