package br.com.persist.data;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
					}
				}
			}
		} catch (IllegalAccessException | InvocationTargetException | InstantiationException ex) {
			LOG.log(Level.SEVERE, ex.getMessage(), ex);
		}
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