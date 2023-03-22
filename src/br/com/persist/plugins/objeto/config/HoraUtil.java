package br.com.persist.plugins.objeto.config;

import java.util.Calendar;

import br.com.persist.plugins.objeto.ObjetoException;

public class HoraUtil {
	public static final byte MINUTO = 60;
	public static final short HORA = 60 * MINUTO;

	private HoraUtil() {
	}

	public static String getHoraAtual() {
		Calendar c = Calendar.getInstance();
		int hor = c.get(Calendar.HOUR_OF_DAY) * HORA;
		int min = c.get(Calendar.MINUTE) * MINUTO;
		int seg = c.get(Calendar.SECOND);
		return formatar(hor + min + seg);
	}

	public static String formatar(int segundos) {
		return Inteiro.formatar(segundos);
	}

	public static int getSegundos(String string) throws ObjetoException {
		return Texto.getSegundos(string);
	}

	public static int getDiff(int valor1, int valor2) {
		return valor1 < valor2 ? valor2 - valor1 : valor1 - valor2;
	}

	public static class Texto {
		private Texto() {
		}

		public static int getTotal(String string, char c) {
			int total = 0;
			if (string != null) {
				for (char d : string.toCharArray()) {
					if (d == c) {
						total++;
					}
				}
			}
			return total;
		}

		public static int getSegundos(String string) throws ObjetoException {
			return getHora(string) + getMinuto(string) + getSegundo(string);
		}

		private static int getHora(String string) throws ObjetoException {
			final String erro = "Hora";
			String[] array = getArray(string, erro);
			String hora = array[0];
			checarLength(hora, erro);
			return getNumero(hora, erro) * HORA;
		}

		private static String[] getArray(String string, String erro) throws ObjetoException {
			String[] array = string.split(":");
			if (array == null || array.length != 3) {
				throw new ObjetoException(erro);
			}
			return array;
		}

		private static void checarLength(String string, String erro) throws ObjetoException {
			if (string.isEmpty()) {
				throw new ObjetoException(erro);
			}
		}

		private static int getMinuto(String string) throws ObjetoException {
			final String erro = "Minuto";
			String[] array = getArray(string, erro);
			String hora = array[1];
			checarLength(hora, erro);
			return getNumero(hora, erro) * MINUTO;
		}

		private static int getSegundo(String string) throws ObjetoException {
			final String erro = "Segundo";
			String[] array = getArray(string, erro);
			String hora = array[2];
			checarLength(hora, erro);
			return getNumero(hora, erro);
		}

		private static byte getNumero(String string, String erro) {
			char c = string.charAt(0);
			while (c == '0') {
				string = string.substring(1);
				if (string.isEmpty()) {
					break;
				} else {
					c = string.charAt(0);
				}
			}
			if (string.isEmpty()) {
				return 0;
			}
			return Byte.parseByte(string);
		}
	}

	public static class Inteiro {
		private Inteiro() {
		}

		public static String formatar(int segundos) {
			int hor = getHora(segundos);
			int min = getMinuto(segundos - (hor * HORA));
			int seg = getSegundo(segundos - (hor * HORA) - (min * MINUTO));
			return get(hor) + ":" + get(min) + ":" + get(seg);
		}

		private static int getHora(int segundos) {
			return segundos / HORA;
		}

		private static int getMinuto(int segundos) {
			return segundos / MINUTO;
		}

		private static int getSegundo(int segundos) {
			return segundos;
		}

		private static String get(long valor) {
			return valor < 10 ? "0" + valor : "" + valor;
		}
	}
}