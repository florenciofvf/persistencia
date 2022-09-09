package br.com.persist.data;

import java.util.Objects;

public class Texto extends Tipo {
	private final String conteudo;

	public Texto(String conteudo) {
		this.conteudo = Objects.requireNonNull(conteudo);
	}

	public String getConteudo() {
		return conteudo;
	}

	@Override
	public String toString() {
		return conteudo;
	}
}