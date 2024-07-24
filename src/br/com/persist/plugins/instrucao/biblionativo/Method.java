package br.com.persist.plugins.instrucao.biblionativo;

public class Method {
	private Method() {
	}

	public static Object get(Object objeto, Object nomeMetodoGet) {
		try {
			Class<?> klass = objeto.getClass();
			java.lang.reflect.Method method = klass.getMethod((java.lang.String) nomeMetodoGet);
			return method.invoke(objeto);
		} catch (Exception ex) {
			throw new IllegalStateException(ex);
		}
	}
}