package br.com.persist.assistencia;

import java.util.Calendar;

public class HoraUtil {
	public static final byte MINUTO = 60;
	public static final short HORA = 60 * MINUTO;
	public static final int OITO_HORAS = HORA * 8;

	private HoraUtil() {
	}

	public static String getHoraAtual() {
		return formatar(getHoraAtualInt());
	}

	public static int getHoraAtualInt() {
		Calendar c = Calendar.getInstance();
		int hora = c.get(Calendar.HOUR_OF_DAY) * HORA;
		int minuto = c.get(Calendar.MINUTE) * MINUTO;
		int segundo = c.get(Calendar.SECOND);
		return hora + minuto + segundo;
	}

	public static String formatar(int segundos) {
		return Inteiro.formatar(segundos);
	}

	public static int getSegundos(String formatado) throws HoraUtilException {
		if (formatado == null) {
			return 0;
		}
		return Texto.getSegundos(formatado);
	}

	public static int getDiff(int valor1, int valor2) {
		return valor1 < valor2 ? valor2 - valor1 : valor1 - valor2;
	}

	public static boolean formatoValido(String string) {
		if (string == null) {
			return false;
		}
		return totalChar(':', string) == 2;
	}

	private static int totalChar(char c, String string) {
		int total = 0;
		for (char d : string.toCharArray()) {
			if (d == c) {
				total++;
			}
		}
		return total;
	}

	private static class Texto {
		private Texto() {
		}

		private static int getSegundos(String formatado) throws HoraUtilException {
			return horas(formatado) + minutos(formatado) + segundos(formatado);
		}

		private static int horas(String formatado) throws HoraUtilException {
			final String erro = "Horas";
			String[] array = array(formatado, erro);
			String hora = array[0];
			checkLength(hora, erro);
			return parseByte(hora) * HORA;
		}

		private static int minutos(String formatado) throws HoraUtilException {
			final String erro = "Minutos";
			String[] array = array(formatado, erro);
			String minuto = array[1];
			checkLength(minuto, erro);
			return parseByte(minuto) * MINUTO;
		}

		private static int segundos(String formatado) throws HoraUtilException {
			final String erro = "Segundos";
			String[] array = array(formatado, erro);
			String segundo = array[2];
			checkLength(segundo, erro);
			return parseByte(segundo);
		}

		private static String[] array(String formatado, String erro) throws HoraUtilException {
			String[] array = formatado.split(":");
			if (array == null || array.length != 3) {
				throw new HoraUtilException(erro);
			}
			return array;
		}

		private static void checkLength(String string, String erro) throws HoraUtilException {
			if (string.isEmpty()) {
				throw new HoraUtilException(erro);
			}
		}

		private static byte parseByte(String string) {
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

	private static class Inteiro {
		private Inteiro() {
		}

		private static String formatar(int segundos) {
			int hora = horas(segundos);
			int minuto = minutos(segundos - (hora * HORA));
			int segundo = segundos(segundos - (hora * HORA) - (minuto * MINUTO));
			return get(hora) + ":" + get(minuto) + ":" + get(segundo);
		}

		private static int horas(int segundos) {
			return segundos / HORA;
		}

		private static int minutos(int segundos) {
			return segundos / MINUTO;
		}

		private static int segundos(int segundos) {
			return segundos;
		}

		private static String get(int valor) {
			return valor < 10 ? "0" + valor : "" + valor;
		}
	}
}