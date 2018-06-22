package br.com.persistencia.args;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import br.com.persistencia.Arg;

public class ArgLong extends Arg {
	private long valor;

	@Override
	public void set(PreparedStatement psmt, int indice) throws Exception {
		psmt.setLong(indice, valor);
	}

	@Override
	public void get(ResultSet rs, int indice) throws Exception {
		valor = rs.getLong(indice);
	}

	@Override
	public void set(StringBuilder sb) {
		sb.append(valor);
	}

	@Override
	public String toString() {
		return "Long=" + valor;
	}

	@Override
	public String getString() {
		return Long.toString(valor);
	}
}