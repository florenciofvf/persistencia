package br.com.persist.data;

import java.awt.Color;
import java.util.Objects;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

public class Texto extends Tipo {
	private static final MutableAttributeSet att;
	private final String conteudo;
	private String alternativo;

	public Texto(String conteudo) {
		this.conteudo = Objects.requireNonNull(conteudo);
	}

	public void export(Container c, int tab) {
		if (alternativo != null) {
			c.append(Formatador.citar(alternativo), att);
		} else {
			c.append(Formatador.citar(conteudo), att);
		}
	}

	public String getConteudo() {
		return conteudo;
	}

	public String getAlternativo() {
		return alternativo;
	}

	public void setAlternativo(String alternativo) {
		this.alternativo = alternativo;
	}

	@Override
	public String toString() {
		return alternativo != null ? alternativo : conteudo;
	}

	static {
		att = new SimpleAttributeSet();
		StyleConstants.setForeground(att, Color.BLUE);
	}
}