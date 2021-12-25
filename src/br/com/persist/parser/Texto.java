package br.com.persist.parser;

import java.awt.Color;
import java.util.Objects;

import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

public class Texto extends Tipo {
	private static final MutableAttributeSet att;
	private final String conteudo;
	private String alternativo;

	public Texto(String conteudo) {
		Objects.requireNonNull(conteudo);
		this.conteudo = conteudo;
	}

	@Override
	public void toString(StringBuilder sb, boolean comTab, int tab) {
		super.toString(sb, comTab, tab);
		if (alternativo != null) {
			sb.append(citar(alternativo));
		} else {
			sb.append(citar(conteudo));
		}
	}

	@Override
	public void toString(AbstractDocument doc, boolean comTab, int tab) throws BadLocationException {
		super.toString(doc, comTab, tab);
		if (alternativo != null) {
			insert(doc, citar(alternativo), att);
		} else {
			insert(doc, citar(conteudo), att);
		}
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
		StyleConstants.setForeground(att, Color.blue);
	}
}