package br.com.persist.data;

import java.util.Objects;

public class Numero extends Tipo {
	private final Object conteudo;

	public Numero(Object conteudo) {
		this.conteudo = Objects.requireNonNull(conteudo);
	}

	public Number getConteudo(Class<?> classe) {
		String string = conteudo.toString();
		if (Double.class.isAssignableFrom(classe)) {
			return Double.valueOf(string);
		} else if (Long.class.isAssignableFrom(classe)) {
			return Long.valueOf(string);
		} else if (Float.class.isAssignableFrom(classe)) {
			return Float.valueOf(string);
		} else if (Integer.class.isAssignableFrom(classe)) {
			return Integer.valueOf(string);
		}
		return null;
	}

	@Override
	public String toString() {
		return conteudo.toString();
	}
}