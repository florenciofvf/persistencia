package br.com.persist.plugins.instrucao.biblionativo;

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
			int posI = string.indexOf(ini) + ini.length();
			int posF = string.indexOf(fim);
			return string.substring(posI, posF);
		}
		return null;
	}

	public void setStringEntre(String ini, String fim, String nova) {
		if (contemExtremos(ini, fim)) {
			int posI = string.indexOf(ini) + ini.length();
			int posF = string.indexOf(fim);
			String inicio = string.substring(0, posI);
			String termino = string.substring(posF);
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