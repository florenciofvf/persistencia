package br.com.persist.fmt;

public class Logico extends Tipo {
	private final boolean conteudo;

	public Logico(boolean conteudo) {
		this.conteudo = conteudo;
	}

	@Override
	public void toString(StringBuilder sb, boolean comTab, int tab) {
		super.toString(sb, comTab, tab);
		sb.append(conteudo ? "true" : "false");
	}
}