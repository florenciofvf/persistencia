package br.com.persist.plugins.expressao.biblionativo;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class NMethod {
	private NMethod() {
	}

	@Biblio
	public static Object get(Object objeto, Object nomeMetodoGet) throws NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Class<?> klass = objeto.getClass();
		Method method = klass.getMethod((String) nomeMetodoGet);
		return method.invoke(objeto);
	}
}