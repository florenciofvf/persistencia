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

	private static boolean isFieldEnum(Field field) {
		Class<?> classe = field.getType();
		Class<?> superClasse = classe.getSuperclass();
		return superClasse != null && "java.lang.Enum".equals(superClasse.getName());
	}

	private static boolean valido(Field field, String coluna) {
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

	protected static Field getField(Class<?> classe, String coluna) {
		if (coluna != null) {
			coluna = coluna.toUpperCase();
			Field[] fields = classe.getDeclaredFields();
			for (Field field : fields) {
				if (isFieldEnum(field) && valido(field, coluna)) {
					return field;
				}
			}
		}
		return null;
	}

	protected static String getDescricaoCampoEnum(Field campo) throws IllegalAccessException {
		StringBuilder sb = new StringBuilder();
		Class<?> tipoCampo = campo.getType();
		Field[] enuns = tipoCampo.getFields();
		for (Field _enum_ : enuns) {
			if (sb.length() > 0) {
				sb.append("\n");
			}
			_enum_.setAccessible(true);
			Object instancia = _enum_.get(campo);
			sb.append(getDescricaoInstanciaEnum(_enum_, instancia));
		}
		return "campo: " + campo.getName() + "\n" + sb.toString();
	}

	private static String getDescricaoInstanciaEnum(Field fieldEnum, Object objeto) throws IllegalAccessException {
		StringBuilder sb = new StringBuilder();
		Class<?> classe = objeto.getClass();
		Field[] campos = classe.getDeclaredFields();
		for (Field campo : campos) {
			if (!Modifier.isStatic(campo.getModifiers())) {
				campo.setAccessible(true);
				if (sb.length() > 0) {
					sb.append(": ");
				}
				sb.append(campo.get(objeto));
			}
		}
		if (sb.length() == 0) {
			sb.append(objeto);
		} else {
			sb.insert(0, fieldEnum.getName() + " = ");
		}
		return sb.toString();
	}
}