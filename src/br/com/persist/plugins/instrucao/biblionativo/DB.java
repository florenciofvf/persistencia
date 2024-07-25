package br.com.persist.plugins.instrucao.biblionativo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class DB {
	private DB() {
	}

	@Biblio
	public static Connection getConnection(Object driver, Object url, Object usuario, Object senha) {
		try {
			Class.forName((java.lang.String) driver);
			return DriverManager.getConnection((java.lang.String) url, (java.lang.String) usuario,
					(java.lang.String) senha);
		} catch (Exception ex) {
			throw new IllegalStateException(ex);
		}
	}

	@Biblio
	public static void closeConnection(Object conexao) {
		try {
			Connection connection = (Connection) conexao;
			if (valido(connection)) {
				connection.close();
			}
		} catch (Exception ex) {
			throw new IllegalStateException(ex);
		}
	}

	private static boolean valido(Connection connection) throws SQLException {
		return connection != null && connection.isValid(1000) && !connection.isClosed();
	}

	@Biblio
	public static Lista select(Object conexao, Object consulta) {
		Lista lista = new Lista();
		Connection connection = (Connection) conexao;
		java.lang.String instrucao = (java.lang.String) consulta;
		try (Statement st = connection.createStatement()) {
			processar(instrucao, st, lista);
		} catch (Exception ex) {
			throw new IllegalStateException(ex);
		}
		return lista;
	}

	private static void processar(java.lang.String instrucao, Statement st, Lista lista) throws SQLException {
		try (ResultSet rs = st.executeQuery(instrucao)) {
			ResultSetMetaData rsmd = rs.getMetaData();
			int qtdColunas = rsmd.getColumnCount();
			if (qtdColunas == 1) {
				while (rs.next()) {
					lista.add(rs.getObject(1));
				}
			} else {
				while (rs.next()) {
					Map<java.lang.String, Object> map = new HashMap<>();
					for (int i = 1; i <= qtdColunas; i++) {
						java.lang.String nome = rsmd.getColumnName(i);
						Object valor = rs.getObject(i);
						map.put(nome, valor);
					}
					lista.add(map);
				}
			}
		}
	}
}