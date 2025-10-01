package br.com.persist.data;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

class NomeValor {
	final String nome;
	final Tipo valor;

	NomeValor(String nome, Tipo valor) {
		this.nome = Objects.requireNonNull(nome);
		this.valor = Objects.requireNonNull(valor);
	}

	boolean contemValor(String string) {
		return valor.contem(string);
	}

	boolean compativel(Method metodoSet) {
		Class<?> param = metodoSet.getParameterTypes()[0];
		if (valor instanceof Logico) {
			return Boolean.class.isAssignableFrom(param);
		} else if (valor instanceof Numero) {
			return Number.class.isAssignableFrom(param) || String.class.isAssignableFrom(param);
		} else if (valor instanceof Texto) {
			return String.class.isAssignableFrom(param);
		}
		return false;
	}

	void invoke(Object object, Method metodoSet) throws IllegalAccessException, InvocationTargetException {
		if (valor instanceof Logico) {
			Boolean arg = ((Logico) valor).getConteudo();
			metodoSet.invoke(object, arg);
		} else if (valor instanceof Numero) {
			Class<?> param = metodoSet.getParameterTypes()[0];
			if (String.class.isAssignableFrom(param)) {
				String arg = ((Numero) valor).getConteudo().toString();
				metodoSet.invoke(object, arg);
			} else {
				Number arg = ((Numero) valor).getConteudo(param);
				metodoSet.invoke(object, arg);
			}
		} else if (valor instanceof Texto) {
			String arg = ((Texto) valor).getConteudo();
			metodoSet.invoke(object, arg);
		}
	}

	@Override
	public String toString() {
		return nome + ": " + valor;
	}

	boolean isNull() {
		return valor instanceof Nulo;
	}

	boolean isObjeto() {
		return valor instanceof Objeto;
	}

	boolean isArray() {
		return valor instanceof Array;
	}

	boolean isTexto() {
		return valor instanceof Texto;
	}

	boolean isAtomico() {
		return valor instanceof Logico || valor instanceof Numero || valor instanceof Texto;
	}
}