package br.com.persistencia.args;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;

import br.com.persistencia.Arg;

public class ArgDate extends Arg {
	private Date valor;

	@Override
	public void set(PreparedStatement psmt, int indice) throws Exception {
		if (valor != null) {
			psmt.setDate(indice, new java.sql.Date(valor.getTime()));
		}
	}

	@Override
	public void get(ResultSet rs, int indice) throws Exception {
		valor = rs.getDate(indice);
	}

	@Override
	public void set(StringBuilder sb) {
		sb.append(valor);
	}

	@Override
	public String toString() {
		return "Date=" + valor;
	}

	@Override
	public String getString() {
		return valor == null ? "" : valor.toString();
	}
}