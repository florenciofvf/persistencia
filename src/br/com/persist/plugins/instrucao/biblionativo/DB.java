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
	public static Connection getConnection(Object driver, Object url, Object usuario, Object senha)
			throws ClassNotFoundException, SQLException {
		java.lang.Class.forName((String) driver);
		return DriverManager.getConnection((String) url, (String) usuario,
				(String) senha);
	}

	@Biblio
	public static void closeConnection(Object conexao) throws SQLException {
		Connection connection = (Connection) conexao;
		if (valido(connection)) {
			connection.close();
		}
	}

	private static boolean valido(Connection connection) throws SQLException {
		return connection != null && connection.isValid(1000) && !connection.isClosed();
	}

	@Biblio
	public static Lista select(Object conexao, Object consulta) throws SQLException {
		Lista lista = new Lista();
		Connection connection = (Connection) conexao;
		String instrucao = (String) consulta;
		try (Statement st = connection.createStatement()) {
			processar(instrucao, st, lista);
		}
		return lista;
	}

	private static void processar(String instrucao, Statement st, Lista lista) throws SQLException {
		try (ResultSet rs = st.executeQuery(instrucao)) {
			ResultSetMetaData rsmd = rs.getMetaData();
			int qtdColunas = rsmd.getColumnCount();
			if (qtdColunas == 1) {
				while (rs.next()) {
					lista.add(rs.getObject(1));
				}
			} else {
				while (rs.next()) {
					Map<String, Object> map = new HashMap<>();
					for (int i = 1; i <= qtdColunas; i++) {
						String nome = rsmd.getColumnName(i);
						Object valor = rs.getObject(i);
						map.put(nome, valor);
					}
					lista.add(map);
				}
			}
		}
	}
}