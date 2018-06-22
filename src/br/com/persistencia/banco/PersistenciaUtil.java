package br.com.persistencia.banco;

import br.com.persistencia.Arg;
import br.com.persistencia.Objeto;

public class PersistenciaUtil {
	private PersistenciaUtil() {
	}

	public static int[] getIndiceParametros(String s) {
		if (s == null) {
			return new int[0];
		}

		char c = '?';
		int indice = s.indexOf(c);

		if (indice == -1) {
			return new int[0];
		}

		Array a = new Array();
		a.add(indice);

		indice = s.indexOf(c, indice + 1);

		while (indice != -1) {
			a.add(indice);
			indice = s.indexOf(c, indice + 1);
		}

		return a.array;
	}

	private static class Array {
		int[] array = new int[1];
		int indice;

		void add(int i) {
			if (indice >= array.length) {
				int[] bkp = array;
				array = new int[bkp.length + 1];
				System.arraycopy(bkp, 0, array, 0, bkp.length);
			}

			array[indice] = i;
			indice++;
		}
	}

	public static String substituir(String instrucao, Objeto objetoArgs) {
		int[] parametros = getIndiceParametros(instrucao);

		return substituir(instrucao, objetoArgs, parametros);
	}

	public static String substituir(String instrucao, Objeto objetoArgs, int[] parametros) {
		StringBuilder builder = new StringBuilder();

		int proximo = 0;

		for (int i = 0; i < parametros.length; i++) {
			Arg arg = objetoArgs.getArgs()[i];
			int indice = parametros[i];

			builder.append(instrucao.substring(proximo, indice));
			arg.set(builder);

			proximo = indice + 1;
		}

		if (proximo < instrucao.length()) {
			builder.append(instrucao.substring(proximo));
		}

		return builder.toString();
	}
}