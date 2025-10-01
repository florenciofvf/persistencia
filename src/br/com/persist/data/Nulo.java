package br.com.persist.data;

import java.awt.Color;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

public class Nulo extends Tipo {
	private static final MutableAttributeSet att;
	public static final String CONTEUDO = "null";

	public void export(Container c, int tab) {
		c.append(CONTEUDO, att);
	}

	@Override
	public void append(Container c, int tab) {
		export(c, tab);
	}

	public String getConteudo() {
		return CONTEUDO;
	}

	@Override
	public String toString() {
		return CONTEUDO;
	}

	@Override
	public boolean contem(String string) {
		return false;
	}

	@Override
	public Tipo clonar() {
		return new Nulo();
	}

	static {
		att = new SimpleAttributeSet();
		StyleConstants.setForeground(att, new Color(255, 0, 255));
	}
}