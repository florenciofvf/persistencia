package br.com.persist.plugins.checagem.arquivo;

import java.io.PrintWriter;

class Linha {
	final int numero;
	final String string;

	Linha(int numero, String string) {
		this.numero = numero;
		this.string = string;
	}

	boolean processar(String str, int num, PrintWriter pw, boolean ln) {
		if (numero == num) {
			if (ln) {
				pw.println(string);
			} else {
				pw.print(string);
			}
			return true;
		} else {
			if (ln) {
				pw.println(str);
			} else {
				pw.print(str);
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return numero + ": " + string;
	}
}