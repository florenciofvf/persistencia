package br.com.persist.fmt;

import java.util.Objects;

public class Numero extends Valor {
	private final String conteudo;

	public Numero(String conteudo) {
		super("Numero");
		Objects.requireNonNull(conteudo);
		this.conteudo = conteudo;
	}

	@Override
	public void fmt(StringBuilder sb, int tab) {
		sb.append(conteudo);
	}
}