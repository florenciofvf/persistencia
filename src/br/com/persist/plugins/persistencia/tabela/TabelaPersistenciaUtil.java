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
		return declaracaoDoCampo(field) + "\n" + descreverInstanciaEnum(field, field.getType(), atual);
	}

	private static String descreverInstanciaEnum(Field campoEnum, Class<?> classeDoCampoEnum, String atual)
			throws IllegalAccessException {
		StringBuilder builder = new StringBuilder();
		Field[] fields = classeDoCampoEnum.getFields();
		for (Field campo : fields) {
			if (builder.length() > 0) {
				builder.append("\n");
			}
			campo.setAccessible(true);
			Object campoInstancia = campo.get(campoEnum);
			builder.append(descreverInstancia(campo, campoInstancia, atual));
		}
		return builder.toString();
	}

	private static String descreverInstancia(Field campo, Object campoInstancia, String atual)
			throws IllegalAccessException {
		StringBuilder builder = new StringBuilder();
		Class<?> classe = campoInstancia.getClass();
		Field[] fields = classe.getDeclaredFields();
		for (Field field : fields) {
			if (!Modifier.isStatic(field.getModifiers())) {
				field.setAccessible(true);
				if (builder.length() > 0) {
					builder.append(": ");
				}
				builder.append(field.get(campoInstancia));
			}
		}
		if (builder.length() == 0) {
			String pre = "";
			if (campoInstancia.toString().equals(atual)) {
				pre = ">>> ";
			}
			builder.append(pre + campoInstancia);
		} else {
			String pre = "";
			if (campo.getName().equals(atual)) {
				pre = ">>> ";
			}
			builder.insert(0, pre + campo.getName() + " = ");
		}
		return builder.toString();
	}

	private static String declaracaoDoCampo(Field field) {
		return field.getType().getName() + " " + field.getName() + ";\n";
	}
}