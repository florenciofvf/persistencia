package br.com.persist.plugins.requisicao.parser;

import java.awt.Color;
import java.util.Objects;

import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

public class Numero extends Tipo {
	private static final MutableAttributeSet att;
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
		StyleConstants.setForeground(att, new Color(0, 125, 0));
	}
}