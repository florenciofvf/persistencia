package br.com.persist.plugins.expressao;

import java.util.List;

import br.com.persist.assistencia.Util;
import br.com.persist.plugins.expressao.compilador.Contexto;
import br.com.persist.plugins.expressao.funcao.FuncaoContexto;

public class ExpressaoUtil {
	private ExpressaoUtil() {
	}

	public static String[] getArray(String string) {
		if (string == null) {
			return new String[0];
		}
		string = Util.trim(string, '.', false);
		string = Util.trim(string, '.', true);
		if (Util.isEmpty(string)) {
			return new String[0];
		}
		return string.split("\\.");
	}

	public static String get(String[] array) {
		if (array == null || array.length == 0) {
			return "";
		}
		StringBuilder sb = new StringBuilder(array[0]);
		for (int i = 1; i < array.length; i++) {
			sb.append(".");
			sb.append(array[i]);
		}
		return sb.toString();
	}

	public static void print(String string, Object object) {
		System.out.println(string + object);
	}

	public static String toString(List<?> lista) {
		StringBuilder builder = new StringBuilder();
		for (Object item : lista) {
			if (builder.length() > 0) {
				builder.append(", ");
			}
			builder.append(item);
		}
		return builder.toString();
	}

	public static String completar(String string) {
		int total = 153;
		StringBuilder builder = new StringBuilder(string);
		while (builder.length() < total) {
			builder.append(' ');
		}
		return builder.toString();
	}

	public static FuncaoContexto getFuncaoContexto(Contexto c) {
		while (c != null) {
			if (c instanceof FuncaoContexto) {
				return (FuncaoContexto) c;
			}
			c = c.getParent();
		}
		return null;
	}
}