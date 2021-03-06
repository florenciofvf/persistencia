package br.com.persist.parser;

import java.awt.Color;
import java.util.Objects;

import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

public class Logico extends Tipo {
	private static final MutableAttributeSet att;
	private final Boolean conteudo;

	public Logico(Boolean conteudo) {
		Objects.requireNonNull(conteudo);
		this.conteudo = conteudo;
	}

	@Override
	public void toString(StringBuilder sb, boolean comTab, int tab) {
		super.toString(sb, comTab, tab);
		sb.append(conteudo.toString());
	}

	@Override
	public void toString(AbstractDocument doc, boolean comTab, int tab) throws BadLocationException {
		super.toString(doc, comTab, tab);
		insert(doc, conteudo.toString(), att);
	}

	@Override
	public String toString() {
		return conteudo.toString();
	}

	static {
		att = new SimpleAttributeSet();
		StyleConstants.setForeground(att, Color.red);
	}
}