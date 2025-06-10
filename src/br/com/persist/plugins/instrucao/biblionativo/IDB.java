package br.com.persist.plugins.instrucao.biblionativo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import br.com.persist.plugins.conexao.Conexao;
import br.com.persist.plugins.conexao.ConexaoException;
import br.com.persist.plugins.conexao.ConexaoProvedor;

public class IDB {
	private IDB() {
	}

	@Biblio(1)
	public static Connection getConnection(Object driver, Object url, Object usuario, Object senha)
			throws ClassNotFoundException, SQLException {
		Class.forName((String) driver);
		return DriverManager.getConnection((String) url, (String) usuario, (String) senha);
	}

	@Biblio(2)
	public static void closeConnection(Object conexao) throws SQLException {
		Connection connection = (Connection) conexao;
		if (valido(connection)) {
			connection.close();
		}
	}

	private static boolean valido(Connection connection) throws SQLException {
		return connection != null && connection.isValid(1000) && !connection.isClosed();
	}

	@Biblio(0)
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

	@Biblio(3)
	public static Conexao getConexao(Object nome) {
		if (nome == null) {
			return null;
		}
		return ConexaoProvedor.getConexao(nome.toString());
	}

	@Biblio(4)
	public static Connection getConnectionProvedor(Object conexao) throws ConexaoException {
		if (conexao instanceof Conexao) {
			return ConexaoProvedor.getConnection((Conexao) conexao);
		}
		return null;
	}

	@Biblio(5)
	public static PreparedStatement createPreparedStatement(Object conexao, Object instrucao) throws SQLException {
		if (conexao instanceof Connection) {
			return ((Connection) conexao).prepareStatement((String) instrucao);
		}
		return null;
	}
}