package br.com.persist.plugins.persistencia.tabela;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import br.com.persist.assistencia.Util;
import br.com.persist.assistencia.Valor;
import br.com.persist.plugins.persistencia.Coluna;
import br.com.persist.plugins.persistencia.OrdenacaoModelo;

public class TabelaPersistenciaUtil {
	private TabelaPersistenciaUtil() {
	}

	public static int getIndiceColuna(TabelaPersistencia tabelaPersistencia, String nome, boolean like) {
		OrdenacaoModelo modelo = tabelaPersistencia.getModelo();
		int qtdColunas = modelo.getColumnCount();
		for (int i = 0; i < qtdColunas; i++) {
			String coluna = modelo.getColumnName(i);
			if (like) {
				coluna = coluna.toUpperCase();
				if (nome != null && coluna.indexOf(nome.toUpperCase()) != -1) {
					return i;
				}
			} else if (coluna.equalsIgnoreCase(nome)) {
				return i;
			}
		}
		return -1;
	}

	public static List<Object[]> getValoresLinha(TabelaPersistencia tabelaPersistencia, Coluna[] colunas,
			boolean apostrofes) {
		OrdenacaoModelo modelo = tabelaPersistencia.getModelo();
		List<Integer> linhas = Util.getIndicesLinha(tabelaPersistencia);
		List<Object[]> resposta = new ArrayList<>();
		for (int i : linhas) {
			Object[] array = new Object[colunas.length];
			for (int j = 0; j < colunas.length; j++) {
				Object obj = modelo.getValueAt(i, colunas[j].getIndice());
				if (obj != null && !Util.isEmpty(obj.toString())) {
					String string = obj.toString();
					array[j] = apostrofes ? Util.citar(string) : string;
				}
			}
			resposta.add(array);
		}
		return resposta;
	}

	public static List<String> getValoresLinha(TabelaPersistencia tabelaPersistencia, int coluna) {
		OrdenacaoModelo modelo = tabelaPersistencia.getModelo();
		List<Integer> linhas = Util.getIndicesLinha(tabelaPersistencia);
		List<String> resposta = new ArrayList<>();
		for (int i : linhas) {
			Object obj = modelo.getValueAt(i, coluna);
			if (obj != null && !Util.isEmpty(obj.toString())) {
				resposta.add(obj.toString());
			}
		}
		return resposta;
	}

	public static Field getFieldParaColuna(Class<?> classe, String coluna) {
		if (classe != null && coluna != null) {
			Field[] fields = classe.getDeclaredFields();
			if (fields != null) {
				coluna = coluna.toUpperCase();
				for (Field field : fields) {
					if (corresponde(field, coluna)) {
						return field;
					}
				}
			}
		}
		return null;
	}

	private static boolean corresponde(Field field, String coluna) {
		if (field.getName().equalsIgnoreCase(coluna)) {
			return true;
		}
		Annotation[] annotations = field.getAnnotations();
		if (annotations != null) {
			for (Annotation annotacao : annotations) {
				String toString = annotacao.toString();
				if (toString != null && toString.toUpperCase().indexOf(coluna) != -1) {
					return true;
				}
			}
		}
		return false;
	}

	public static String descreverField(Field field, List<Valor> valores)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		if (field == null) {
			return "";
		}
		return !ehCampoEnum(field) ? declaracaoDoCampo(field) : declaracaoDoCampoEnum(field, valores);
	}

	private static boolean ehCampoEnum(Field field) {
		Class<?> classe = field.getType();
		Class<?> superClasse = classe.getSuperclass();
		return superClasse != null && Enum.class.isAssignableFrom(superClasse);
	}

	private static String declaracaoDoCampo(Field field) {
		return field.getType().getName() + " " + field.getName() + ";\n";
	}

	private static String declaracaoDoCampoEnum(Field field, List<Valor> valores)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		return declaracaoDoCampo(field) + "\n" + descreverEnum(field, valores);
	}

	private static String descreverEnum(Field campoEnum, List<Valor> valores)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Class<?> classeDoCampo = campoEnum.getType();
		Object[] enums = classeDoCampo.getEnumConstants();
		StringBuilder builder = new StringBuilder();
		for (Object instancia : enums) {
			if (builder.length() > 0) {
				builder.append("\n");
			}
			if (sel(instancia, valores)) {
				builder.append(">>> ");
			}
			builder.append(instancia);
			Method[] metodosGet = getMetodosGet(instancia);
			if (metodosGet.length > 0) {
				builder.append(" = (");
				builder.append(detalharInstancia(instancia, metodosGet));
				builder.append(")");
			}
		}
		return builder.toString();
	}

	private static boolean sel(Object instancia, List<Valor> valores) {
		for (Valor valor : valores) {
			if (igual(instancia, valor)) {
				return true;
			}
		}
		return false;
	}

	private static boolean igual(Object instancia, Valor valor) {
		if (instancia instanceof Enum) {
			Enum<?> enumeration = (Enum<?>) instancia;
			if (valor.isNumerico()) {
				return Integer.toString(enumeration.ordinal()).equals(valor.getString());
			} else {
				return enumeration.name().equals(valor.getString());
			}
		}
		return false;
	}

	private static Method[] getMetodosGet(Object instancia) {
		Class<?> classe = instancia.getClass();
		Method[] methods = classe.getDeclaredMethods();
		List<Method> resp = new ArrayList<>();
		for (Method method : methods) {
			if (validoGet(method)) {
				resp.add(method);
			}
		}
		return resp.toArray(new Method[0]);
	}

	private static boolean validoGet(Method method) {
		if (method.getName().startsWith("get")) {
			return method.getParameterCount() == 0;
		}
		return false;
	}

	private static String detalharInstancia(Object instancia, Method[] metodosGet)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		StringBuilder builder = new StringBuilder();
		for (Method method : metodosGet) {
			if (builder.length() > 0) {
				builder.append(", ");
			}
			builder.append(nome(method.getName()) + "->");
			builder.append(method.invoke(instancia));
		}
		return builder.toString();
	}

	private static String nome(String string) {
		String name = string.substring(3);
		if (name.isEmpty()) {
			return string;
		}
		String pri = name.substring(0, 1).toLowerCase();
		if (name.length() < 2) {
			return pri;
		}
		return pri + name.substring(1);
	}
}