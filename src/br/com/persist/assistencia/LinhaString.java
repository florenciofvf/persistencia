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

	public boolean stringEqual(String str) {
		return string != null && string.equals(str);
	}

	public boolean numeroEqual(long num) {
		return numero == num;
	}

	public boolean iniciaEfinalizaCom(String ini, String fim) {
		return string != null && string.startsWith(ini) && string.endsWith(fim);
	}

	public String stringEntre(String ini, String fim) {
		if (iniciaEfinalizaCom(ini, fim)) {
			int posI = string.indexOf(ini) + ini.length();
			int posF = string.indexOf(fim);
			return string.substring(posI, posF);
		}
		return null;
	}

	public String stringEntreReplace(String ini, String fim, String nova) {
		if (iniciaEfinalizaCom(ini, fim)) {
			int posI = string.indexOf(ini) + ini.length();
			int posF = string.indexOf(fim);
			String inicio = string.substring(0, posI);
			String termino = string.substring(posF);
			return inicio + nova + termino;
		}
		return null;
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
		return numero + ": " + string + "\n";
	}
}