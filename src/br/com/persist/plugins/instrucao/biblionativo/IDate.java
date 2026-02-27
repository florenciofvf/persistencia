package br.com.persist.plugins.instrucao.biblionativo;

import java.time.LocalDate;

public class IDate {
	private IDate() {
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
	public static Integer getDiaAtual() {
		LocalDate localDate = LocalDate.now();
		return localDate.getDayOfMonth();
	}
}