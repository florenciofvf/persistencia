package br.com.persist.assistencia;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <pre>
 * setNome %%% java.lang.String @@@ Novo nome
 * 
 * ###
 * 
 * metodoQQ
 * 
 * %%% java.lang.Long @@@ 48
 * 
 * %%% br.com.teste.MesesEnum @@@ JANEIRO
 * </pre>
 */
public class ReflexaoUtil {
	private static final String SEPARADOR_PARAMETROS = "%%%";
	private static final String SEPARADOR_METODOS = "###";
	private static final String SEPARADOR_VALOR = "@@@";
	public static final String VALOR_NULL = "NULL";

	private ReflexaoUtil() {
	}

	public static Object processar(String classe, String construtorID, String metodos) throws ReflexaoException {
		Class<?> klass = getKlass(classe);
		Objeto id = criarObjeto(construtorID);
		Object object = getObject(klass, id);

		List<Metodo> listaMetodo = criarMetodos(klass, metodos);

		for (Metodo item : listaMetodo) {
			item.processar(object);
		}

		return object;
	}

	private static Class<?> getKlass(String classe) throws ReflexaoException {
		try {
			return Class.forName(classe);
		} catch (Exception ex) {
			throw new ReflexaoException("Erro em ReflexaoUtil.getClasse ->" + classe);
		}
	}

	static Objeto criarObjeto(String classeValor) throws ReflexaoException {
		String[] array = classeValor.split(SEPARADOR_VALOR);
		String classe = array[0].trim();
		String valor = array[1].trim();

		Class<?> klasse = getKlass(classe);
		Objeto objeto = null;

		if (klasse.isEnum()) {
			objeto = new ObjetoEnum();
		} else if (Date.class.equals(klasse)) {
			objeto = new ObjetoDate();
		} else {
			objeto = new ObjetoComum();
		}

		objeto.set(klasse, valor);
		return objeto;
	}

	private static Object getObject(Class<?> klass, Objeto id) throws ReflexaoException {
		try {
			/** para ambiente JPA: return entityManaget.find(klass, id.valor); */
			Constructor<?> constructor = klass.getConstructor(id.klass);
			return constructor.newInstance(id.valor);
		} catch (Exception ex) {
			throw new ReflexaoException("Erro em ReflexaoUtil.getObject ->" + klass + "->" + id);
		}
	}

	private static List<Metodo> criarMetodos(Class<?> klass, String metodos) throws ReflexaoException {
		String[] metodosESeusParametros = metodos.split(SEPARADOR_METODOS);
		List<Metodo> listaMetodo = new ArrayList<>();
		for (String item : metodosESeusParametros) {
			Metodo metodo = criar(klass, item);
			listaMetodo.add(metodo);
		}
		return listaMetodo;
	}

	private static Metodo criar(Class<?> klass, String nomeEParametros) throws ReflexaoException {
		String[] parametros = nomeEParametros.split(SEPARADOR_PARAMETROS);
		String nome = parametros[0].trim();
		Metodo metodo = new Metodo(nome);
		metodo.setMethod(klass, parametros);
		return metodo;
	}
}

class Metodo {
	final List<Objeto> listaObjeto;
	final String nome;
	Method method;

	public Metodo(String nome) {
		listaObjeto = new ArrayList<>();
		this.nome = nome;
	}

	void setMethod(Class<?> klasse, String[] parametros) throws ReflexaoException {
		for (int i = 1; i < parametros.length; i++) {
			Objeto objeto = ReflexaoUtil.criarObjeto(parametros[i]);
			listaObjeto.add(objeto);
		}
		try {
			method = klasse.getMethod(nome, getArrayClass());
		} catch (Exception ex) {
			throw new ReflexaoException("Erro em Metodo.configMethod ->" + klasse + "->" + parametros);
		}
	}

	private Class<?>[] getArrayClass() {
		Class<?>[] array = new Class<?>[listaObjeto.size()];
		for (int i = 0; i < listaObjeto.size(); i++) {
			array[i] = listaObjeto.get(i).klass;
		}
		return array;
	}

	void processar(Object object) throws ReflexaoException {
		try {
			method.invoke(object, getArrayValor());
		} catch (Exception ex) {
			throw new ReflexaoException("Erro em Metodo.processar ->" + nome + "->" + object);
		}
	}

	private Object[] getArrayValor() {
		Object[] array = new Object[listaObjeto.size()];
		for (int i = 0; i < listaObjeto.size(); i++) {
			array[i] = listaObjeto.get(i).valor;
		}
		return array;
	}
}

abstract class Objeto {
	Class<?> klass;
	Object valor;

	abstract void set(Class<?> klass, String parametro) throws ReflexaoException;

	@Override
	public String toString() {
		return klass + "=" + valor;
	}
}

class ObjetoEnum extends Objeto {
	@Override
	public void set(Class<?> klass, String parametro) throws ReflexaoException {
		this.klass = klass;
		if (ReflexaoUtil.VALOR_NULL.equalsIgnoreCase(parametro)) {
			valor = null;
			return;
		}
		Object[] array = klass.getEnumConstants();
		for (Object item : array) {
			if (parametro.equals(item.toString())) {
				valor = item;
			}
		}
	}
}

class ObjetoComum extends Objeto {
	@Override
	public void set(Class<?> klass, String parametro) throws ReflexaoException {
		try {
			this.klass = klass;
			if (ReflexaoUtil.VALOR_NULL.equalsIgnoreCase(parametro)) {
				valor = null;
				return;
			}
			Constructor<?> constructor = klass.getConstructor(String.class);
			valor = constructor.newInstance(parametro);
		} catch (Exception ex) {
			throw new ReflexaoException("Erro em ObjetoComum.set ->" + klass + "->" + parametro);
		}
	}
}

class ObjetoDate extends Objeto {
	@Override
	public void set(Class<?> klass, String parametro) throws ReflexaoException {
		try {
			this.klass = klass;
			if (ReflexaoUtil.VALOR_NULL.equalsIgnoreCase(parametro)) {
				valor = null;
				return;
			}
			boolean contemHora = parametro.indexOf(':') != -1;
			DateFormat format = contemHora ? new SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
					: new SimpleDateFormat("dd/MM/yyyy");
			valor = format.parse(parametro);
		} catch (Exception ex) {
			throw new ReflexaoException("Erro em ObjetoDate.set ->" + klass + "->" + parametro);
		}
	}
}