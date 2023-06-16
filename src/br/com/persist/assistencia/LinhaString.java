package br.com.persist.assistencia;

import java.io.PrintWriter;

public class LinhaString {
	final String string;
	final long numero;
	final char cr;
	final char lf;

	public LinhaString(long numero, String string, char cr, char lf) {
		this.numero = numero;
		this.string = string;
		this.cr = cr;
		this.lf = lf;
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

	public LinhaString clonar(String string) {
		return new LinhaString(numero, string, cr, lf);
	}

	public void print(PrintWriter pw, LinhaString linhaString, long num) {
		if (numero == num) {
			print(pw);
		} else {
			linhaString.print(pw);
		}
	}

	private void print(PrintWriter pw) {
		pw.print(string);
		if (cr != 0) {
			pw.print(cr);
		}
		if (lf != 0) {
			pw.print(lf);
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(numero + ": " + string);
		if (cr != 0) {
			sb.append(cr);
		}
		if (lf != 0) {
			sb.append(lf);
		}
		return sb.toString();
	}
}