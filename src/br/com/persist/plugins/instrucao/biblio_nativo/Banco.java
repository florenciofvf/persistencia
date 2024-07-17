package br.com.persist.plugins.instrucao.biblio_nativo;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import br.com.persist.assistencia.Lista;

public class Banco {
	private Banco() {
	}

	public static Connection getConnection(Object driver, Object url, Object usuario, Object senha) {
		try {
			Class.forName((String) driver);
			return DriverManager.getConnection((String) url, (String) usuario, (String) senha);
		} catch (Exception ex) {
			throw new IllegalStateException(ex);
		}
	}

	public static BigInteger closeConnection(Object conexao) {
		try {
			Connection connection = (Connection) conexao;
			if (valido(connection)) {
				connection.close();
				return BigInteger.valueOf(1);
			}
		} catch (Exception ex) {
			throw new IllegalStateException(ex);
		}
		return BigInteger.valueOf(0);
	}

	private static boolean valido(Connection connection) throws SQLException {
		return connection != null && connection.isValid(1000) && !connection.isClosed();
	}

	public static Lista select(Object conexao, Object consulta) {
		Lista lista = new Lista();
		Connection connection = (Connection) conexao;
		String instrucao = (String) consulta;
		try (Statement st = connection.createStatement()) {
			processar(instrucao, st, lista);
		} catch (Exception ex) {
			throw new IllegalStateException(ex);
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