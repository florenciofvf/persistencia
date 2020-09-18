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
	public void toString(AbstractDocument doc, boolean comTab, int tab) throws BadLocationException {
		super.toString(doc, comTab, tab);
		insert(doc, citar(conteudo), att);
	}

	@Override
	public String toString() {
		return conteudo;
	}

	static {
		att = new SimpleAttributeSet();
		StyleConstants.setForeground(att, Color.blue);
	}
}