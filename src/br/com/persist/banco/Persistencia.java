package br.com.persist.banco;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.persist.Objeto;
import br.com.persist.modelo.ModeloRegistro;
import br.com.persist.tabela.Coluna;
import br.com.persist.util.Util;

public class Persistencia {

	public static int executar(String sql, Connection conn) throws Exception {
		PreparedStatement psmt = conn.prepareStatement(sql);
		int i = psmt.executeUpdate();
		psmt.close();
		return i;
	}

	public static int getTotalRegistros(Connection conn, Objeto objeto, String complemento) throws Exception {
		StringBuilder builder = new StringBuilder("SELECT COUNT(*) FROM " + objeto.getTabela());
		if (!Util.estaVazio(complemento)) {
			builder.append(" WHERE 1=1 " + complemento);
		}

		PreparedStatement psmt = conn.prepareStatement(builder.toString());
		ResultSet rs = psmt.executeQuery();
		rs.next();

		int total = rs.getInt(1);

		rs.close();
		psmt.close();

		return total;
	}

	public static ModeloRegistro criarModeloRegistro(Connection conn, String consulta, String[] chaves, Objeto objeto)
			throws Exception {
		PreparedStatement psmt = conn.prepareStatement(consulta);

		ResultSet rs = psmt.executeQuery();
		ModeloRegistro modelo = criarModelo(rs, chaves, objeto.getTabela());

		rs.close();
		psmt.close();

		return modelo;
	}

	private static ModeloRegistro criarModelo(ResultSet rs, String[] chaves, String tabela) throws Exception {
		Map<String, Boolean> mapa = new HashMap<>();

		mapa.put("java.math.BigDecimal", Boolean.TRUE);
		mapa.put("java.math.BigInteger", Boolean.TRUE);
		mapa.put("java.lang.Character", Boolean.FALSE);
		mapa.put("java.lang.Boolean", Boolean.FALSE);
		mapa.put("java.lang.Integer", Boolean.TRUE);
		mapa.put("java.lang.String", Boolean.FALSE);
		mapa.put("java.lang.Double", Boolean.TRUE);
		mapa.put("java.lang.Date", Boolean.FALSE);
		mapa.put("java.lang.Float", Boolean.TRUE);
		mapa.put("java.lang.Short", Boolean.TRUE);
		mapa.put("java.lang.Long", Boolean.TRUE);
		mapa.put("java.lang.Byte", Boolean.TRUE);

		ResultSetMetaData rsmd = rs.getMetaData();
		int qtdColunas = rsmd.getColumnCount();

		List<Coluna> colunas = new ArrayList<>();

		for (int i = 1; i <= qtdColunas; i++) {
			String classe = rsmd.getColumnClassName(i);
			String nome = rsmd.getColumnName(i).trim();
			Boolean numero = mapa.get(classe);
			int tipo = rsmd.getColumnType(i);
			Boolean chave = false;

			if (numero == null) {
				numero = Boolean.FALSE;
			}

			for (String s : chaves) {
				if (s.trim().equalsIgnoreCase(nome)) {
					chave = Boolean.TRUE;
				}
			}

			Coluna coluna = new Coluna(nome, i - 1, numero, chave, tipo == Types.BLOB || tipo == Types.LONGVARBINARY);
			colunas.add(coluna);
		}

		List<List<Object>> registros = new ArrayList<>();

		while (rs.next()) {
			List<Object> registro = new ArrayList<>();

			for (int i = 1; i <= qtdColunas; i++) {
				Object valor = colunas.get(i - 1).isBlob() ? "BLOB" : rs.getString(i);
				registro.add(valor == null ? "" : valor);
			}

			registros.add(registro);
		}

		return new ModeloRegistro(colunas, registros, tabela);
	}
}