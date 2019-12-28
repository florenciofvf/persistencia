package br.com.persist.fmt;

import java.util.Objects;

public class Numero extends Tipo {
	private final Number conteudo;

	public Numero(Number conteudo) {
		Objects.requireNonNull(conteudo);
		this.conteudo = conteudo;
	}

	@Override
	public void toString(StringBuilder sb, boolean comTab, int tab) {
		super.toString(sb, comTab, tab);
		sb.append(conteudo.toString());
	}

	@Override
	public String toString() {
		return conteudo.toString();
	}
}