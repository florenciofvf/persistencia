package br.com.persist.plugins.persistencia.tabela;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import br.com.persist.assistencia.Util;
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

	public static List<String> getValoresLinha(TabelaPersistencia tabelaPersistencia, int coluna) {
		OrdenacaoModelo modelo = tabelaPersistencia.getModelo();
		List<Integer> linhas = Util.getIndicesLinha(tabelaPersistencia);
		List<String> resposta = new ArrayList<>();
		for (int i : linhas) {
			Object obj = modelo.getValueAt(i, coluna);
			if (obj != null && !Util.estaVazio(obj.toString())) {
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

	private static String declaracaoDoCampo(Field field) {
		return field.getType().getName() + " " + field.getName() + ";\n";
	}

	public static String descreverField(Field field, String atual) throws IllegalAccessException {
		if (field == null) {
			return "";
		}
		return ehCampoEnum(field) ? declaracaoDoCampoEnum(field, atual) : declaracaoDoCampo(field);
	}

	private static boolean ehCampoEnum(Field field) {
		Class<?> classe = field.getType();
		Class<?> superClasse = classe.getSuperclass();
		return superClasse != null && Enum.class.isAssignableFrom(superClasse);
	}

	private static String declaracaoDoCampoEnum(Field field, String atual) throws IllegalAccessException {
		return declaracaoDoCampo(field) + "\n" + descreverInstanciaEnum(field, atual);
	}

	private static String descreverInstanciaEnum(Field campoEnum, String atual) throws IllegalAccessException {
		Class<?> classeDoCampoEnum = campoEnum.getType();
		Field[] fieldsEnum = classeDoCampoEnum.getFields();
		boolean comparacaoOrdinal = ehNumero(atual);
		StringBuilder builder = new StringBuilder();
		for (Field fieldEnum : fieldsEnum) {
			if (builder.length() > 0) {
				builder.append("\n");
			}
			fieldEnum.setAccessible(true);
			Object fieldInstancia = fieldEnum.get(campoEnum);
			builder.append(descreverInstancia(fieldEnum, fieldInstancia, atual, comparacaoOrdinal));
		}
		return builder.toString();
	}

	private static boolean ehNumero(String atual) {
		if (Util.estaVazio(atual)) {
			return false;
		}
		for (char c : atual.toCharArray()) {
			if (c < '0' || c > '9') {
				return false;
			}
		}
		return true;
	}

	private static String descreverInstancia(Field fieldEnum, Object fieldInstancia, String atual,
			boolean comparacaoOrdinal) throws IllegalAccessException {
		StringBuilder builder = new StringBuilder();
		Class<?> classe = fieldInstancia.getClass();
		Field[] fields = classe.getDeclaredFields();
		if (igual(fieldInstancia, atual, comparacaoOrdinal)) {
			builder.append(">>> ");
		}
		if (contemValido(fields)) {
			builder.append(fieldEnum.getName() + " = [" + getCampos(fields, fieldInstancia) + "]");
		} else {
			builder.append(fieldInstancia);
		}
		return builder.toString();
	}

	private static boolean igual(Object instancia, String valor, boolean comparacaoOrdinal) {
		if (instancia instanceof Enum) {
			Enum<?> enumeration = (Enum<?>) instancia;
			if (comparacaoOrdinal) {
				return Integer.toString(enumeration.ordinal()).equals(valor);
			} else {
				return enumeration.name().equals(valor);
			}
		}
		return false;
	}

	private static boolean contemValido(Field[] fields) {
		for (Field field : fields) {
			if (!Modifier.isStatic(field.getModifiers())) {
				return true;
			}
		}
		return false;
	}

	private static String getCampos(Field[] fields, Object campoInstancia) throws IllegalAccessException {
		StringBuilder builder = new StringBuilder();
		for (Field field : fields) {
			if (!Modifier.isStatic(field.getModifiers())) {
				field.setAccessible(true);
				if (builder.length() > 0) {
					builder.append(", ");
				}
				builder.append(field.get(campoInstancia));
			}
		}
		return builder.toString();
	}
}