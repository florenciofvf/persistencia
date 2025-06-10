package br.com.persist.plugins.instrucao.biblionativo;

import java.io.Reader;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class IPsmt {
	private IPsmt() {
	}

	@Biblio(0)
	public static void setInteger(Object psmt, Object indice, Object valor) throws SQLException {
		PreparedStatement obj = (PreparedStatement) psmt;
		obj.setInt(((Number) indice).intValue(), ((Number) valor).intValue());
	}

	@Biblio(1)
	public static void setString(Object psmt, Object indice, Object valor) throws SQLException {
		PreparedStatement obj = (PreparedStatement) psmt;
		obj.setString(((Number) indice).intValue(), (String) valor);
	}

	@Biblio(2)
	public static void setReader(Object psmt, Object indice, Object reader) throws SQLException {
		PreparedStatement obj = (PreparedStatement) psmt;
		obj.setCharacterStream(((Number) indice).intValue(), (Reader) reader);
	}

	@Biblio(3)
	public static void executeUpdate(Object psmt) throws SQLException {
		PreparedStatement obj = (PreparedStatement) psmt;
		obj.executeUpdate();
	}

	@Biblio(4)
	public static void closePsmt(Object psmt) throws SQLException {
		PreparedStatement obj = (PreparedStatement) psmt;
		obj.close();
	}
}