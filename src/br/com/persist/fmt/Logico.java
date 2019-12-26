package br.com.persist.fmt;

import java.util.Objects;

public class Logico extends Valor {
	private final Boolean conteudo;

	public Logico(Boolean conteudo) {
		super("Logico");
		Objects.requireNonNull(conteudo);
		this.conteudo = conteudo;
	}

	@Override
	public void fmt(StringBuilder sb, int tab) {
		sb.append(conteudo.toString());
	}
}