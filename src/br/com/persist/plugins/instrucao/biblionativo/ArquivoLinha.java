package br.com.persist.plugins.instrucao.biblionativo;

import java.io.PrintWriter;

public class ArquivoLinha {
	final String string;
	final long numero;
	final char cr;
	final char lf;

	public ArquivoLinha(long numero, String string, char cr, char lf) {
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

	public boolean stringEqual(String str, boolean trim) {
		if (string != null) {
			return trim ? string.trim().equals(str) : string.equals(str);
		}
		return false;
	}

	public boolean numeroEqual(long num) {
		return numero == num;
	}

	public boolean iniciaEfinalizaCom(String ini, String fim, boolean trim) {
		if (string != null) {
			String str = trim ? string.trim() : string;
			return str.startsWith(ini) && str.endsWith(fim);
		}
		return false;
	}

	public String stringEntre(String ini, String fim, boolean trim) {
		if (iniciaEfinalizaCom(ini, fim, trim)) {
			int posI = string.indexOf(ini) + ini.length();
			int posF = string.indexOf(fim);
			return string.substring(posI, posF);
		}
		return null;
	}

	public String stringEntreReplace(String ini, String fim, String nova,
			boolean trim) {
		if (iniciaEfinalizaCom(ini, fim, trim)) {
			int posI = string.indexOf(ini) + ini.length();
			int posF = string.indexOf(fim);
			String inicio = string.substring(0, posI);
			String termino = string.substring(posF);
			return inicio + nova + termino;
		}
		return null;
	}

	public ArquivoLinha clonar(String string) {
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