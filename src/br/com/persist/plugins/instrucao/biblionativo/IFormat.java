package br.com.persist.plugins.instrucao.biblionativo;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

public class IFormat {
	private static final Formatador FORMATADOR = new Formatador();

	private IFormat() {
	}

	@Biblio(0)
	public static String date(Object date) {
		return FORMATADOR.date(date);
	}

	@Biblio(1)
	public static String time(Object date) {
		return FORMATADOR.time(date);
	}

	@Biblio(2)
	public static String dateTime(Object date) {
		return FORMATADOR.dateTime(date);
	}

	static class Formatador {
		private Format formatDateTime = new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss");
		private Format formatDate = new SimpleDateFormat("dd/MM/yyyy");
		private Format formatTime = new SimpleDateFormat("HH:mm:ss");

		String date(Object object) {
			if (object instanceof Date) {
				return formatDate.format((Date) object);
			}
			return "";
		}

		String time(Object object) {
			if (object instanceof Date) {
				return formatTime.format((Date) object);
			}
			return "";
		}

		String dateTime(Object object) {
			if (object instanceof Date) {
				return formatDateTime.format((Date) object);
			}
			return "";
		}
	}
}