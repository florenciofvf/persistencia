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
		CHAVE("[a-z][A-Z][0-9]"), CHAVE2("chave.chave"), CHAVEN("chave.chave.chave"), PONTO_E_VIRGULA(";"),
		ABRE_PARENTESE("("), FECHA_PARENTESE(")"), ABRE_CHAVE("["), FECHA_CHAVE("]"), IGUAL("=="), DIFERENTE("!="),
		ATRIBUICAO("=");

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

	public boolean isChave() {
		return tipo == Tipo.CHAVE;
	}

	public boolean isChave2() {
		return tipo == Tipo.CHAVE2;
	}

	public boolean isChaveN() {
		return tipo == Tipo.CHAVEN;
	}
}