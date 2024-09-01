package br.com.persist.plugins.instrucao.biblionativo;

import java.io.PrintWriter;

public class ILinha {
	final String string;
	final long numero;

	public ILinha(long numero, String string) {
		this.numero = numero;
		this.string = string;
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

	public String stringEntreReplace(String ini, String fim, String nova, boolean trim) {
		if (iniciaEfinalizaCom(ini, fim, trim)) {
			int posI = string.indexOf(ini) + ini.length();
			int posF = string.indexOf(fim);
			String inicio = string.substring(0, posI);
			String termino = string.substring(posF);
			return inicio + nova + termino;
		}
		return null;
	}

	public ILinha clonar(String string) {
		return new ILinha(numero, string);
	}

	public void print(PrintWriter pw, ILinha linha, long num) {
		if (numero == num) {
			print(pw);
		} else {
			linha.print(pw);
		}
	}

	public void print(PrintWriter pw) {
		pw.print(string);
	}

	@Override
	public String toString() {
		return numero + ": " + string;
	}
}