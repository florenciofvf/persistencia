package br.com.persist.data;

import java.util.Objects;

public class Logico extends Tipo {
	private final Boolean conteudo;

	public Logico(Boolean conteudo) {
		this.conteudo = Objects.requireNonNull(conteudo);
	}

	public Boolean getConteudo() {
		return conteudo;
	}

	@Override
	public String toString() {
		return conteudo.toString();
	}
}