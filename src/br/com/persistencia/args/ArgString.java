package br.com.persistencia.args;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import br.com.persistencia.Arg;

public class ArgString extends Arg {
	private String valor;

	@Override
	public void set(PreparedStatement psmt, int indice) throws Exception {
		psmt.setString(indice, valor);
	}

	@Override
	public void get(ResultSet rs, int indice) throws Exception {
		valor = rs.getString(indice);
	}

	@Override
	public void set(StringBuilder sb) {
		sb.append(valor);
	}

	@Override
	public String toString() {
		return "String=" + valor;
	}

	@Override
	public String getString() {
		return valor == null ? "" : valor;
	}
}