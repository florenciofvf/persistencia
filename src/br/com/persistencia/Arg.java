package br.com.persistencia;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import br.com.persistencia.args.ArgDate;
import br.com.persistencia.args.ArgInt;
import br.com.persistencia.args.ArgLong;
import br.com.persistencia.args.ArgObject;
import br.com.persistencia.args.ArgString;

public abstract class Arg {

	public abstract void set(PreparedStatement psmt, int indice) throws Exception;

	public abstract void get(ResultSet rs, int indice) throws Exception;

	public abstract void set(StringBuilder sb);

	public abstract String getString();

	public static Arg criar(String tipo, String outro) {
		outro = outro == null ? "" : outro.toLowerCase();
		tipo = tipo == null ? "" : tipo.toLowerCase();

		if (tipo.startsWith("longo_") || outro.startsWith("longo_")) {
			return new ArgLong();

		} else if (tipo.startsWith("inteiro_") || outro.startsWith("inteiro_")) {
			return new ArgInt();

		} else if (tipo.startsWith("data_") || outro.startsWith("data_")) {
			return new ArgDate();

		} else if (tipo.startsWith("texto_") || outro.startsWith("texto_")) {
			return new ArgString();

		} else if (tipo.startsWith("objeto_") || outro.startsWith("objeto_")) {
			return new ArgObject();
		}

		return new ArgString();
	}
}