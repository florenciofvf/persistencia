package br.com.persistencia.args;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import br.com.persistencia.Arg;

public class ArgObject extends Arg {
	private Object valor;

	@Override
	public void set(PreparedStatement psmt, int indice) throws Exception {
		psmt.setObject(indice, valor);
	}

	@Override
	public void get(ResultSet rs, int indice) throws Exception {
		valor = rs.getObject(indice);
	}

	@Override
	public void set(StringBuilder sb) {
		sb.append(valor);
	}

	@Override
	public String toString() {
		return "Object=" + valor;
	}

	@Override
	public String getString() {
		return valor == null ? "" : valor.toString();
	}
}