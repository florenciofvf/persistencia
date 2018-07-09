package br.com.persist.tabela;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.persist.banco.Conexao;

public class Persistencia {

	public static ModeloRegistro criarModeloRegistro(String consulta, String[] chaves) throws Exception {
		Connection conn = Conexao.getConnection();
		PreparedStatement psmt = conn.prepareStatement(consulta);

		ResultSet rs = psmt.executeQuery();
		ModeloRegistro modelo = criarModelo(rs, chaves);

		rs.close();
		psmt.close();

		return modelo;
	}

	private static ModeloRegistro criarModelo(ResultSet rs, String[] chaves) throws Exception {
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
			Boolean chave = false;

			for (String s : chaves) {
				if (s.trim().equals(nome)) {
					chave = Boolean.TRUE;
				}
			}

			Coluna coluna = new Coluna(nome, i - 1, numero == null ? Boolean.FALSE : numero, chave);
			colunas.add(coluna);
		}

		List<List<Object>> registros = new ArrayList<>();

		while (rs.next()) {
			List<Object> registro = new ArrayList<>();

			for (int i = 1; i <= qtdColunas; i++) {
				Object valor = rs.getString(i);
				registro.add(valor == null ? "" : valor);
			}

			registros.add(registro);
		}

		return new ModeloRegistro(colunas, registros);
	}
}