package br.com.persist.plugins.expressao.biblionativo;

import java.time.LocalDate;

public class NData {
	private NData() {
	}

	@Biblio(0)
	public static Integer getAnoAtual() {
		LocalDate localDate = LocalDate.now();
		return localDate.getYear();
	}

	@Biblio(1)
	public static Integer getMesAtual() {
		LocalDate localDate = LocalDate.now();
		return localDate.getMonth().getValue();
	}

	@Biblio(2)
	public static String getMesAtualFmt() {
		LocalDate localDate = LocalDate.now();
		return fmt(localDate.getMonth().getValue());
	}

	@Biblio(3)
	public static Integer getDiaAtual() {
		LocalDate localDate = LocalDate.now();
		return localDate.getDayOfMonth();
	}

	@Biblio(4)
	public static String getDiaAtualFmt() {
		LocalDate localDate = LocalDate.now();
		return fmt(localDate.getDayOfMonth());
	}

	private static String fmt(int i) {
		return i < 10 ? "0" + i : "" + i;
	}
}