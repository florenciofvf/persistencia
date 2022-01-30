package br.com.persist.parser;

public class TipoUtil {

	private TipoUtil() {
	}

	public static String toString(Tipo tipo) {
		StringBuilder sb = new StringBuilder();
		if (tipo != null) {
			tipo.toString(sb, true, 0);
		}
		return sb.toString();
	}
}