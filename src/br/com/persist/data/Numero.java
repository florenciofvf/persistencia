package br.com.persist.data;

import java.awt.Color;
import java.util.Objects;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

public class Numero extends Tipo {
	private static final MutableAttributeSet att;
	private final Object conteudo;

	public Numero(Object conteudo) {
		this.conteudo = Objects.requireNonNull(conteudo);
	}

	public void export(Container c, int tab) {
		c.append(conteudo.toString(), att);
	}

	@Override
	public void append(Container c, int tab) {
		export(c, tab);
	}

	public Object getConteudo() {
		return conteudo;
	}

	public Number getConteudo(Class<?> classe) {
		String string = conteudo.toString();
		if (Double.class.isAssignableFrom(classe)) {
			return Double.valueOf(string);
		} else if (Float.class.isAssignableFrom(classe)) {
			return Float.valueOf(string);
		} else if (Long.class.isAssignableFrom(classe)) {
			return Long.valueOf(string);
		} else if (Integer.class.isAssignableFrom(classe)) {
			return Integer.valueOf(string);
		} else if (Short.class.isAssignableFrom(classe)) {
			return Short.valueOf(string);
		} else if (Byte.class.isAssignableFrom(classe)) {
			return Byte.valueOf(string);
		}
		return null;
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
		return new Numero(conteudo);
	}

	static {
		att = new SimpleAttributeSet();
		StyleConstants.setForeground(att, new Color(0, 125, 0));
	}
}