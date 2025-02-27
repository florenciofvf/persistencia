package br.com.persist.plugins.instrucao.biblionativo;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class IParse {
	private static final Conversor CONVERSOR = new Conversor();

	private IParse() {
	}

	@Biblio(0)
	public static BigInteger bigInteger(Object object) {
		return new BigInteger(object.toString());
	}

	@Biblio(1)
	public static BigDecimal bigDecimal(Object object) {
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