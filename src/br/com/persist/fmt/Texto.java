package br.com.persist.fmt;

import java.util.Objects;

public class Texto extends Tipo {
	private final String conteudo;

	public Texto(String conteudo) {
		Objects.requireNonNull(conteudo);
		this.conteudo = conteudo;
	}

	@Override
	public void toString(StringBuilder sb, boolean comTab, int tab) {
		super.toString(sb, comTab, tab);
		sb.append(citar(conteudo));
	}

	@Override
	public String toString() {
		return conteudo;
	}
}