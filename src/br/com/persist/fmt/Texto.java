package br.com.persist.fmt;

import java.util.Objects;

public class Texto extends Valor {
	private final String conteudo;

	public Texto(String conteudo) {
		super("Texto");
		Objects.requireNonNull(conteudo);
		this.conteudo = conteudo;
	}

	@Override
	public void fmt(StringBuilder sb, int tab) {
		sb.append(citar(conteudo));
	}
}