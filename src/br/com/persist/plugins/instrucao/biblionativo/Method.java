package br.com.persist.plugins.instrucao.biblionativo;

import java.lang.reflect.InvocationTargetException;

public class Method {
	private Method() {
	}

	@Biblio
	public static Object get(Object objeto, Object nomeMetodoGet) throws NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		java.lang.Class<?> klass = objeto.getClass();
		java.lang.reflect.Method method = klass.getMethod((java.lang.String) nomeMetodoGet);
		return method.invoke(objeto);
	}
}