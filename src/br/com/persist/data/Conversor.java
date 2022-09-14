package br.com.persist.data;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Conversor {
	private static final Logger LOG = Logger.getGlobal();

	private Conversor() {
	}

	public static void converter(Objeto objeto, Object object) {
		try {
			PoolMetodo poolMetodo = new PoolMetodo(object.getClass());
			for (NomeValor nomeValor : objeto.getAtributos()) {
				if (nomeValor.isNull()) {
					continue;
				}
				Method metodoSet = poolMetodo.getMethodSet(nomeValor.nome);
				if (metodoSet != null) {
					if (nomeValor.compativel(metodoSet)) {
						nomeValor.invoke(object, metodoSet);
					} else if (nomeValor.isObjeto()) {
						Class<?> classeParam = metodoSet.getParameterTypes()[0];
						Object objetoParam = classeParam.newInstance();
						metodoSet.invoke(object, objetoParam);
						((Objeto) nomeValor.valor).converter(objetoParam);
					} else if (nomeValor.isArray()) {
						String classe = getNomeClasse(metodoSet);
						if (classe != null) {
							Class<?> klass = Class.forName(classe);
							Object[] array = ((Array) nomeValor.valor).converter(klass);
							List<Object> lista = Arrays.asList(array);
							metodoSet.invoke(object, lista);
						}
					}
				}
			}
		} catch (IllegalAccessException | InvocationTargetException | ClassNotFoundException
				| InstantiationException ex) {
			LOG.log(Level.SEVERE, ex.getMessage(), ex);
		}
	}

	private static String getNomeClasse(Method metodo) {
		Class<?> classeParam = metodo.getParameterTypes()[0];
		if (!List.class.isAssignableFrom(classeParam)) {
			return null;
		}
		Type[] types = metodo.getGenericParameterTypes();
		if (types == null || types.length != 1) {
			return null;
		}
		Type type = types[0];
		if (!(type instanceof ParameterizedType)) {
			return null;
		}
		ParameterizedType typeParam = (ParameterizedType) type;
		Type[] typesArgs = typeParam.getActualTypeArguments();
		if (typesArgs == null || typesArgs.length != 1) {
			return null;
		}
		return typesArgs[0].getTypeName();
	}

	public static Object[] converter(Array array, Class<?> classe) {
		List<Tipo> elementos = array.getElementos();
		Object[] resposta = new Object[elementos.size()];
		try {
			for (int i = 0; i < elementos.size(); i++) {
				Tipo tipo = elementos.get(i);
				if (tipo instanceof Objeto) {
					Object object = classe.newInstance();
					resposta[i] = ((Objeto) tipo).converter(object);
				}
			}
		} catch (IllegalAccessException | InstantiationException ex) {
			LOG.log(Level.SEVERE, ex.getMessage(), ex);
		}
		return resposta;
	}

	static class PoolMetodo {
		final Method[] metodos;

		PoolMetodo(Class<?> classe) {
			metodos = classe.getDeclaredMethods();
		}

		Method getMethodSet(String nome) {
			String nomeMetodo = "set" + nome.substring(0, 1).toUpperCase() + nome.substring(1);
			for (Method m : metodos) {
				if (nomeMetodo.equals(m.getName())) {
					Class<?>[] parameterTypes = m.getParameterTypes();
					if (parameterTypes.length == 1) {
						return m;
					}
				}
			}
			return null;
		}
	}
}