package br.com.persist.plugins.instrucao.biblionativo;

import java.lang.reflect.Method;

public class Reflection {
	private Reflection() {
	}

	public static Object methodGet(Object objeto, Object nomeMetodoGet) {
		try {
			Class<?> klass = objeto.getClass();
			Method method = klass.getMethod((java.lang.String) nomeMetodoGet);
			return method.invoke(objeto);
		} catch (Exception ex) {
			throw new IllegalStateException(ex);
		}
	}
}