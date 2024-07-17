package br.com.persist.plugins.instrucao.nat;

import java.lang.reflect.Method;

public class Reflection {
	private Reflection() {
	}

	public static Object invokeGet(Object objeto, Object nomeMetodoGet) {
		try {
			Class<?> klass = objeto.getClass();
			Method method = klass.getMethod((String) nomeMetodoGet);
			return method.invoke(objeto);
		} catch (Exception ex) {
			throw new IllegalStateException(ex);
		}
	}
}