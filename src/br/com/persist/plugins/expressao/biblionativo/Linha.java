package br.com.persist.plugins.expressao.biblionativo;

import java.io.PrintWriter;

public class Linha {
	final long numero;
	String string;

	public Linha(long numero, String string) {
		this.numero = numero;
		this.string = string;
	}

	public long getNumero() {
		return numero;
	}

	public String getString() {
		return string;
	}

	public void setString(String string) {
		this.string = string;
	}

	public boolean contem(String str) {
		String s = string.trim();
		return s.indexOf(str) != -1;
	}

	public boolean contemExtremos(String ini, String fim) {
		String s = string.trim();
		return s.startsWith(ini) && s.endsWith(fim);
	}

	public String getStringEntre(String ini, String fim) {
		if (contemExtremos(ini, fim)) {
			int posIni = string.indexOf(ini) + ini.length();
			int posFim = string.indexOf(fim);
			return string.substring(posIni, posFim);
		}
		return null;
	}

	public void setStringEntre(String ini, String fim, String nova) {
		if (contemExtremos(ini, fim)) {
			int posIni = string.indexOf(ini) + ini.length();
			int posFim = string.indexOf(fim);
			String inicio = string.substring(0, posIni);
			String termino = string.substring(posFim);
			string = inicio + nova + termino;
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