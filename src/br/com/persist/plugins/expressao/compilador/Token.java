package br.com.persist.plugins.expressao.compilador;

import java.util.Objects;

public class Token {
	final String string;
	final int indice;
	final Tipo tipo;

	public Token(String string, Tipo tipo, int indice) {
		this.string = Objects.requireNonNull(string);
		this.indice = Objects.requireNonNull(indice);
		this.tipo = Objects.requireNonNull(tipo);
	}

	public enum Tipo {
		PONTO_E_VIRGULA(";"), ABRE_PARENTESE("("), IGUAL("=="), ATRIBUICAO("=");

		private String desc;

		Tipo(String desc) {
			this.desc = desc;
		}

		public String getDesc() {
			return desc;
		}
	}

	public boolean isPontoEVirgula() {
		return tipo == Tipo.PONTO_E_VIRGULA;
	}

	public boolean isAbreParentese() {
		return tipo == Tipo.ABRE_PARENTESE;
	}
}