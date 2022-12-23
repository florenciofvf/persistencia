package br.com.persist.plugins.objeto.config;

import br.com.persist.plugins.objeto.ObjetoException;

public class HoraUtil {
	public static final byte SESSENTA = 60;

	private HoraUtil() {
	}

	public static String formatar(int horaMin) {
		long hor = getHora(horaMin);
		long min = getMinuto(horaMin);
		return get(hor) + ":" + get(min);
	}

	public static int getDiff(int valor1, int valor2) {
		return valor1 < valor2 ? valor2 - valor1 : valor1 - valor2;
	}

	public static int getTime(String horaMin) throws ObjetoException {
		return getHora(horaMin) + getMinuto(horaMin);
	}

	private static int getHora(String horaMin) throws ObjetoException {
		int indice = horaMin.indexOf(':');
		final String erro = "Hora";
		if (indice == -1) {
			throw new ObjetoException(erro);
		}
		String hora = horaMin.substring(0, indice);
		checarLength(hora, erro);
		return getNumero(hora, erro) * SESSENTA;
	}

	private static int getMinuto(String horaMin) throws ObjetoException {
		int indice = horaMin.indexOf(':');
		final String erro = "Minuto";
		if (indice == -1) {
			throw new ObjetoException(erro);
		}
		String hora = horaMin.substring(indice + 1);
		checarLength(hora, erro);
		return getNumero(hora, erro);
	}

	private static int getHora(int horaMin) {
		return horaMin / SESSENTA;
	}

	private static int getMinuto(int horaMin) {
		return horaMin % SESSENTA;
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

	private static void checarLength(String string, String erro) throws ObjetoException {
		if (string.isEmpty()) {
			throw new ObjetoException(erro);
		}
	}

	private static String get(long valor) {
		return valor < 10 ? "0" + valor : "" + valor;
	}
}