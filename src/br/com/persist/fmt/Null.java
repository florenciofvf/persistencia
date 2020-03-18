package br.com.persist.fmt;

import java.awt.Color;
import java.util.Objects;

import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

public class Null extends Tipo {
	public static final String CONTEUDO = "null";
	private static final MutableAttributeSet att;

	public Null() {
		Objects.requireNonNull(CONTEUDO);
	}

	@Override
	public void toString(StringBuilder sb, boolean comTab, int tab) {
		super.toString(sb, comTab, tab);
		sb.append(CONTEUDO);
	}

	@Override
	public void toString(AbstractDocument doc, boolean comTab, int tab) throws BadLocationException {
		super.toString(doc, comTab, tab);
		insert(doc, CONTEUDO, att);
	}

	@Override
	public String toString() {
		return CONTEUDO;
	}

	static {
		att = new SimpleAttributeSet();
		StyleConstants.setForeground(att, new Color(255, 0, 255));
	}
}