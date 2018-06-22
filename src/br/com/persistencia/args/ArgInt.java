package br.com.persistencia.args;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import br.com.persistencia.Arg;

public class ArgInt extends Arg {
	private int valor;

	@Override
	public void set(PreparedStatement psmt, int indice) throws Exception {
		psmt.setInt(indice, valor);
	}

	@Override
	public void get(ResultSet rs, int indice) throws Exception {
		valor = rs.getInt(indice);
	}

	@Override
	public void set(StringBuilder sb) {
		sb.append(valor);
	}

	@Override
	public String toString() {
		return "Int=" + valor;
	}

	@Override
	public String getString() {
		return Integer.toString(valor);
	}
}