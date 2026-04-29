package br.com.persist.plugins.expressao.biblionativo;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class NParse {
	private static final Conversor CONVERSOR = new Conversor();

	private NParse() {
	}

	@Biblio(0)
	public static BigInteger createBigInteger(Object object) {
		return new BigInteger(object.toString());
	}

	@Biblio(1)
	public static BigDecimal createBigDecimal(Object object) {
		return new BigDecimal(object.toString());
	}

	@Biblio(2)
	public static Object dateUS(Object stringUS) {
		return CONVERSOR.dateUS(stringUS);
	}

	static class Conversor {
		private DateFormat dateFormatUS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

		Object dateUS(Object object) {
			if (object == null) {
				return "";
			}
			String string = object.toString();
			try {
				return dateFormatUS.parse(string);
			} catch (Exception ex) {
				return "";
			}
		}
	}
}