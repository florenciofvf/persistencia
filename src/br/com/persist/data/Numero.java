package br.com.persist.data;

import java.util.Objects;

public class Numero implements Tipo {
	private final Object conteudo;

	public Numero(Object conteudo) {
		this.conteudo = Objects.requireNonNull(conteudo);
	}

	@Override
	public String toString() {
		return conteudo.toString();
	}
}