package br.com.persist.plugins.instrucao.biblionativo;

import java.io.PrintWriter;

public class ArquivoLinha {
	final java.lang.String string;
	final long numero;
	final char cr;
	final char lf;

	public ArquivoLinha(long numero, java.lang.String string, char cr, char lf) {
		this.numero = numero;
		this.string = string;
		this.cr = cr;
		this.lf = lf;
	}

	public long getNumero() {
		return numero;
	}

	public java.lang.String getString() {
		return string;
	}

	public boolean stringEqual(java.lang.String str, boolean trim) {
		if (string != null) {
			return trim ? string.trim().equals(str) : string.equals(str);
		}
		return false;
	}

	public boolean numeroEqual(long num) {
		return numero == num;
	}

	public boolean iniciaEfinalizaCom(java.lang.String ini, java.lang.String fim, boolean trim) {
		if (string != null) {
			java.lang.String str = trim ? string.trim() : string;
			return str.startsWith(ini) && str.endsWith(fim);
		}
		return false;
	}

	public java.lang.String stringEntre(java.lang.String ini, java.lang.String fim, boolean trim) {
		if (iniciaEfinalizaCom(ini, fim, trim)) {
			int posI = string.indexOf(ini) + ini.length();
			int posF = string.indexOf(fim);
			return string.substring(posI, posF);
		}
		return null;
	}

	public java.lang.String stringEntreReplace(java.lang.String ini, java.lang.String fim, java.lang.String nova,
			boolean trim) {
		if (iniciaEfinalizaCom(ini, fim, trim)) {
			int posI = string.indexOf(ini) + ini.length();
			int posF = string.indexOf(fim);
			java.lang.String inicio = string.substring(0, posI);
			java.lang.String termino = string.substring(posF);
			return inicio + nova + termino;
		}
		return null;
	}

	public ArquivoLinha clonar(java.lang.String string) {
		return new ArquivoLinha(numero, string, cr, lf);
	}

	public void print(PrintWriter pw, ArquivoLinha arquivo, long num) {
		if (numero == num) {
			print(pw);
		} else {
			arquivo.print(pw);
		}
	}

	public void print(PrintWriter pw) {
		pw.print(string);
		if (cr != 0) {
			pw.print(cr);
		}
		if (lf != 0) {
			pw.print(lf);
		}
	}

	@Override
	public java.lang.String toString() {
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