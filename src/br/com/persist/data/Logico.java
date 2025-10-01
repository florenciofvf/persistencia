package br.com.persist.data;

import java.awt.Color;
import java.util.Objects;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

public class Logico extends Tipo {
	private static final MutableAttributeSet att;
	private final Boolean conteudo;

	public Logico(Boolean conteudo) {
		this.conteudo = Objects.requireNonNull(conteudo);
	}

	public void export(Container c, int tab) {
		c.append(conteudo.toString(), att);
	}

	@Override
	public void append(Container c, int tab) {
		export(c, tab);
	}

	public Boolean getConteudo() {
		return conteudo;
	}

	@Override
	public String toString() {
		return conteudo.toString();
	}

	@Override
	public boolean contem(String string) {
		return toString().contains(string);
	}

	@Override
	public Tipo clonar() {
		return new Logico(conteudo);
	}

	static {
		att = new SimpleAttributeSet();
		StyleConstants.setForeground(att, Color.RED);
	}
}