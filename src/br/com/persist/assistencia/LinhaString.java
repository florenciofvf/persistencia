package br.com.persist.assistencia;

import java.io.PrintWriter;

public class LinhaString {
	final String string;
	final long numero;

	public LinhaString(long numero, String string) {
		this.numero = numero;
		this.string = string;
	}

	public long getNumero() {
		return numero;
	}

	public String getString() {
		return string;
	}

	public void print(PrintWriter pw, String str, long num, boolean ln) {
		if (numero == num) {
			if (ln) {
				pw.println(string);
			} else {
				pw.print(string);
			}
		} else {
			if (ln) {
				pw.println(str);
			} else {
				pw.print(str);
			}
		}
	}

	@Override
	public String toString() {
		return numero + ": " + string;
	}
}