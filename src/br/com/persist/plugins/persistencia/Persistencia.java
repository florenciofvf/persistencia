package br.com.persist.plugins.persistencia;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Preferencias;
import br.com.persist.assistencia.Util;
import br.com.persist.plugins.conexao.Conexao;

public class Persistencia {
	private static final String FKCOLUMN_NAME = "FKCOLUMN_NAME";
	private static final String PKCOLUMN_NAME = "PKCOLUMN_NAME";
	private static final String TABLE_CATALOG = "TABLE_CATALOG";
	private static final String FKTABLE_NAME = "FKTABLE_NAME";
	private static final String PKTABLE_NAME = "PKTABLE_NAME";
	private static final String TABLE_SCHEM = "TABLE_SCHEM";
	private static final String COLUMN_NAME = "COLUMN_NAME";
	private static final String TABLE_NAME = "TABLE_NAME";
	private static final String KEY_SEQ = "KEY_SEQ";
	private static final String PK_NAME = "PK_NAME";
	private static final Map<String, Boolean> mapa;

	private Persistencia() {
	}

	static {
		mapa = new HashMap<>();
		mapa.put("java.math.BigDecimal", Boolean.TRUE);
		mapa.put("java.math.BigInteger", Boolean.TRUE);
		mapa.put("java.lang.Character", Boolean.FALSE);
		mapa.put("java.sql.Timestamp", Boolean.FALSE);
		mapa.put("java.lang.Boolean", Boolean.FALSE);
		mapa.put("java.lang.Integer", Boolean.TRUE);
		mapa.put("java.lang.String", Boolean.FALSE);
		mapa.put("java.lang.Double", Boolean.TRUE);
		mapa.put("java.lang.Float", Boolean.TRUE);
		mapa.put("java.lang.Short", Boolean.TRUE);
		mapa.put("java.lang.Long", Boolean.TRUE);
		mapa.put("java.lang.Byte", Boolean.TRUE);
		mapa.put("java.sql.Date", Boolean.FALSE);
	}

	private static String normal(String string) {
		if (string == null) {
			return string;
		}
		StringBuilder sb = new StringBuilder(string.trim());
		while (sb.length() > 0 && (normalLast(sb, ';') || normalLast(sb, ' '))) {
			sb.delete(sb.length() - 1, sb.length());
		}
		return sb.toString();
	}

	private static boolean normalLast(StringBuilder sb, char c) {
		return sb.charAt(sb.length() - 1) == c;
	}

	public static int executar(Connection conn, String sql) throws PersistenciaException {
		try (PreparedStatement psmt = conn.prepareStatement(normal(sql))) {
			return psmt.executeUpdate();
		} catch (Exception ex) {
			throw new PersistenciaException(ex);
		}
	}

	private static Map<String, Boolean> criarMapaTipos() {
		return mapa;
	}

	public static MemoriaModelo criarModeloInfoBanco(Connection conn) throws PersistenciaException {
		try {
			DatabaseMetaData m = conn.getMetaData();
			List<List<String>> dados = new ArrayList<>();
			dados.add(criar("allProceduresAreCallable", m.allProceduresAreCallable()));
			dados.add(criar("allTablesAreSelectable", m.allTablesAreSelectable()));
			dados.add(criar("getURL", m.getURL()));
			dados.add(criar("isReadOnly", m.isReadOnly()));
			dados.add(criar("nullsAreSortedHigh", m.nullsAreSortedHigh()));
			dados.add(criar("nullsAreSortedLow", m.nullsAreSortedLow()));
			dados.add(criar("nullsAreSortedAtStart", m.nullsAreSortedAtStart()));
			dados.add(criar("nullsAreSortedAtEnd", m.nullsAreSortedAtEnd()));
			dados.add(criar("getDatabaseProductName", m.getDatabaseProductName()));
			dados.add(criar("getDatabaseProductVersion", m.getDatabaseProductVersion()));
			dados.add(criar("getDriverName", m.getDriverName()));
			dados.add(criar("getDriverVersion", m.getDriverVersion()));
			dados.add(criar("getDriverMajorVersion", m.getDriverMajorVersion()));
			dados.add(criar("getDriverMinorVersion", m.getDriverMinorVersion()));
			dados.add(criar("usesLocalFiles", m.usesLocalFiles()));
			dados.add(criar("usesLocalFilePerTable", m.usesLocalFilePerTable()));
			dados.add(criar("supportsMixedCaseIdentifiers", m.supportsMixedCaseIdentifiers()));
			dados.add(criar("storesUpperCaseIdentifiers", m.storesUpperCaseIdentifiers()));
			dados.add(criar("storesLowerCaseIdentifiers", m.storesLowerCaseIdentifiers()));
			dados.add(criar("storesMixedCaseIdentifiers", m.storesMixedCaseIdentifiers()));
			dados.add(criar("supportsMixedCaseQuotedIdentifiers", m.supportsMixedCaseQuotedIdentifiers()));
			dados.add(criar("storesUpperCaseQuotedIdentifiers", m.storesUpperCaseQuotedIdentifiers()));
			dados.add(criar("storesLowerCaseQuotedIdentifiers", m.storesLowerCaseQuotedIdentifiers()));
			dados.add(criar("storesMixedCaseQuotedIdentifiers", m.storesMixedCaseQuotedIdentifiers()));
			dados.add(criar("getIdentifierQuoteString", m.getIdentifierQuoteString()));
			dados.add(criar("getSQLKeywords", m.getSQLKeywords()));
			dados.add(criar("getNumericFunctions", m.getNumericFunctions()));
			dados.add(criar("getStringFunctions", m.getStringFunctions()));
			dados.add(criar("getSystemFunctions", m.getSystemFunctions()));
			dados.add(criar("getTimeDateFunctions", m.getTimeDateFunctions()));
			dados.add(criar("getSearchStringEscape", m.getSearchStringEscape()));
			dados.add(criar("getExtraNameCharacters", m.getExtraNameCharacters()));
			dados.add(criar("supportsAlterTableWithAddColumn", m.supportsAlterTableWithAddColumn()));
			dados.add(criar("supportsAlterTableWithDropColumn", m.supportsAlterTableWithDropColumn()));
			dados.add(criar("supportsColumnAliasing", m.supportsColumnAliasing()));
			dados.add(criar("nullPlusNonNullIsNull", m.nullPlusNonNullIsNull()));
			dados.add(criar("supportsConvert", m.supportsConvert()));
			dados.add(criar("supportsTableCorrelationNames", m.supportsTableCorrelationNames()));
			dados.add(criar("supportsDifferentTableCorrelationNames", m.supportsDifferentTableCorrelationNames()));
			dados.add(criar("supportsExpressionsInOrderBy", m.supportsExpressionsInOrderBy()));
			dados.add(criar("supportsOrderByUnrelated", m.supportsOrderByUnrelated()));
			dados.add(criar("supportsGroupBy", m.supportsGroupBy()));
			dados.add(criar("supportsGroupByUnrelated", m.supportsGroupByUnrelated()));
			dados.add(criar("supportsGroupByBeyondSelect", m.supportsGroupByBeyondSelect()));
			dados.add(criar("supportsLikeEscapeClause", m.supportsLikeEscapeClause()));
			dados.add(criar("supportsMultipleResultSets", m.supportsMultipleResultSets()));
			dados.add(criar("supportsMultipleTransactions", m.supportsMultipleTransactions()));
			dados.add(criar("supportsNonNullableColumns", m.supportsNonNullableColumns()));
			dados.add(criar("supportsMinimumSQLGrammar", m.supportsMinimumSQLGrammar()));
			dados.add(criar("supportsCoreSQLGrammar", m.supportsCoreSQLGrammar()));
			dados.add(criar("supportsExtendedSQLGrammar", m.supportsExtendedSQLGrammar()));
			dados.add(criar("supportsANSI92EntryLevelSQL", m.supportsANSI92EntryLevelSQL()));
			dados.add(criar("supportsANSI92IntermediateSQL", m.supportsANSI92IntermediateSQL()));
			dados.add(criar("supportsANSI92FullSQL", m.supportsANSI92FullSQL()));
			dados.add(criar("supportsIntegrityEnhancementFacility", m.supportsIntegrityEnhancementFacility()));
			dados.add(criar("supportsOuterJoins", m.supportsOuterJoins()));
			dados.add(criar("supportsFullOuterJoins", m.supportsFullOuterJoins()));
			dados.add(criar("supportsLimitedOuterJoins", m.supportsLimitedOuterJoins()));
			dados.add(criar("getSchemaTerm", m.getSchemaTerm()));
			dados.add(criar("getProcedureTerm", m.getProcedureTerm()));
			dados.add(criar("getCatalogTerm", m.getCatalogTerm()));
			dados.add(criar("isCatalogAtStart", m.isCatalogAtStart()));
			dados.add(criar("getCatalogSeparator", m.getCatalogSeparator()));
			dados.add(criar("supportsSchemasInDataManipulation", m.supportsSchemasInDataManipulation()));
			dados.add(criar("supportsSchemasInProcedureCalls", m.supportsSchemasInProcedureCalls()));
			dados.add(criar("supportsSchemasInTableDefinitions", m.supportsSchemasInTableDefinitions()));
			dados.add(criar("supportsSchemasInIndexDefinitions", m.supportsSchemasInIndexDefinitions()));
			dados.add(criar("supportsSchemasInPrivilegeDefinitions", m.supportsSchemasInPrivilegeDefinitions()));
			dados.add(criar("supportsCatalogsInDataManipulation", m.supportsCatalogsInDataManipulation()));
			dados.add(criar("supportsCatalogsInProcedureCalls", m.supportsCatalogsInProcedureCalls()));
			dados.add(criar("supportsCatalogsInTableDefinitions", m.supportsCatalogsInTableDefinitions()));
			dados.add(criar("supportsCatalogsInIndexDefinitions", m.supportsCatalogsInIndexDefinitions()));
			dados.add(criar("supportsCatalogsInPrivilegeDefinitions", m.supportsCatalogsInPrivilegeDefinitions()));
			dados.add(criar("supportsPositionedDelete", m.supportsPositionedDelete()));
			dados.add(criar("supportsPositionedUpdate", m.supportsPositionedUpdate()));
			dados.add(criar("supportsSelectForUpdate", m.supportsSelectForUpdate()));
			dados.add(criar("supportsStoredProcedures", m.supportsStoredProcedures()));
			dados.add(criar("supportsSubqueriesInComparisons", m.supportsSubqueriesInComparisons()));
			dados.add(criar("supportsSubqueriesInExists", m.supportsSubqueriesInExists()));
			dados.add(criar("supportsSubqueriesInIns", m.supportsSubqueriesInIns()));
			dados.add(criar("supportsSubqueriesInQuantifieds", m.supportsSubqueriesInQuantifieds()));
			dados.add(criar("supportsCorrelatedSubqueries", m.supportsCorrelatedSubqueries()));
			dados.add(criar("supportsUnion", m.supportsUnion()));
			dados.add(criar("supportsUnionAll", m.supportsUnionAll()));
			dados.add(criar("supportsOpenCursorsAcrossCommit", m.supportsOpenCursorsAcrossCommit()));
			dados.add(criar("supportsOpenCursorsAcrossRollback", m.supportsOpenCursorsAcrossRollback()));
			dados.add(criar("supportsOpenStatementsAcrossCommit", m.supportsOpenStatementsAcrossCommit()));
			dados.add(criar("supportsOpenStatementsAcrossRollback", m.supportsOpenStatementsAcrossRollback()));
			dados.add(criar("getMaxBinaryLiteralLength", m.getMaxBinaryLiteralLength()));
			dados.add(criar("getMaxCharLiteralLength", m.getMaxCharLiteralLength()));
			dados.add(criar("getMaxColumnNameLength", m.getMaxColumnNameLength()));
			dados.add(criar("getMaxColumnsInGroupBy", m.getMaxColumnsInGroupBy()));
			dados.add(criar("getMaxColumnsInIndex", m.getMaxColumnsInIndex()));
			dados.add(criar("getMaxColumnsInOrderBy", m.getMaxColumnsInOrderBy()));
			dados.add(criar("getMaxColumnsInSelect", m.getMaxColumnsInSelect()));
			dados.add(criar("getMaxColumnsInTable", m.getMaxColumnsInTable()));
			dados.add(criar("getMaxConnections", m.getMaxConnections()));
			dados.add(criar("getMaxCursorNameLength", m.getMaxCursorNameLength()));
			dados.add(criar("getMaxIndexLength", m.getMaxIndexLength()));
			dados.add(criar("getMaxSchemaNameLength", m.getMaxSchemaNameLength()));
			dados.add(criar("getMaxProcedureNameLength", m.getMaxProcedureNameLength()));
			dados.add(criar("getMaxCatalogNameLength", m.getMaxCatalogNameLength()));
			dados.add(criar("getMaxRowSize", m.getMaxRowSize()));
			dados.add(criar("doesMaxRowSizeIncludeBlobs", m.doesMaxRowSizeIncludeBlobs()));
			dados.add(criar("getMaxStatementLength", m.getMaxStatementLength()));
			dados.add(criar("getMaxStatements", m.getMaxStatements()));
			dados.add(criar("getMaxTableNameLength", m.getMaxTableNameLength()));
			dados.add(criar("getMaxTablesInSelect", m.getMaxTablesInSelect()));
			dados.add(criar("getMaxUserNameLength", m.getMaxUserNameLength()));
			dados.add(criar("getDefaultTransactionIsolation", m.getDefaultTransactionIsolation()));
			dados.add(criar("supportsTransactions", m.supportsTransactions()));
			dados.add(criar("supportsDataDefinitionAndDataManipulationTransactions",
					m.supportsDataDefinitionAndDataManipulationTransactions()));
			dados.add(criar("supportsDataManipulationTransactionsOnly", m.supportsDataManipulationTransactionsOnly()));
			dados.add(criar("dataDefinitionCausesTransactionCommit", m.dataDefinitionCausesTransactionCommit()));
			dados.add(criar("dataDefinitionIgnoredInTransactions", m.dataDefinitionIgnoredInTransactions()));
			dados.add(criar("supportsBatchUpdates", m.supportsBatchUpdates()));
			dados.add(criar("supportsSavepoints", m.supportsSavepoints()));
			dados.add(criar("supportsNamedParameters", m.supportsNamedParameters()));
			dados.add(criar("supportsMultipleOpenResults", m.supportsMultipleOpenResults()));
			dados.add(criar("supportsGetGeneratedKeys", m.supportsGetGeneratedKeys()));
			dados.add(criar("getResultSetHoldability", m.getResultSetHoldability()));
			dados.add(criar("getDatabaseMajorVersion", m.getDatabaseMajorVersion()));
			dados.add(criar("getDatabaseMinorVersion", m.getDatabaseMinorVersion()));
			dados.add(criar("getJDBCMajorVersion", m.getJDBCMajorVersion()));
			dados.add(criar("getJDBCMinorVersion", m.getJDBCMinorVersion()));
			dados.add(criar("getSQLStateType", m.getSQLStateType()));
			dados.add(criar("locatorsUpdateCopy", m.locatorsUpdateCopy()));
			dados.add(criar("supportsStatementPooling", m.supportsStatementPooling()));
			dados.add(criar("supportsStoredFunctionsUsingCallSyntax", m.supportsStoredFunctionsUsingCallSyntax()));
			dados.add(criar("autoCommitFailureClosesAllResultSets", m.autoCommitFailureClosesAllResultSets()));
			dados.add(criar("generatedKeyAlwaysReturned", m.generatedKeyAlwaysReturned()));
			dados.add(criar("getMaxLogicalLobSize", m.getMaxLogicalLobSize()));
			dados.add(criar("supportsRefCursors", m.supportsRefCursors()));
			return new MemoriaModelo(Arrays.asList("NOME", "VALOR"), dados);
		} catch (Exception ex) {
			throw new PersistenciaException(ex);
		}
	}

	private static List<String> criar(String nome, Object object) {
		return Arrays.asList(nome, object == null ? Constantes.VAZIO : object.toString());
	}

	public static MemoriaModelo criarModeloEsquema(Connection conn) throws PersistenciaException {
		try {
			List<List<String>> dados = new ArrayList<>();
			DatabaseMetaData m = conn.getMetaData();
			ResultSet rs = m.getSchemas();
			while (rs.next()) {
				dados.add(criar(rs.getString(TABLE_SCHEM), rs.getString(TABLE_CATALOG)));
			}
			rs.close();
			return new MemoriaModelo(Arrays.asList(TABLE_SCHEM, TABLE_CATALOG), dados);
		} catch (Exception ex) {
			throw new PersistenciaException(ex);
		}
	}

	public static String[] getTotalRegistros(Connection conn, String aposFROM) throws PersistenciaException {
		StringBuilder builder = new StringBuilder("SELECT COUNT(*) FROM " + aposFROM);
		String[] array = new String[2];
		array[0] = normal(builder.toString());
		try (PreparedStatement psmt = conn.prepareStatement(array[0])) {
			try (ResultSet rs = psmt.executeQuery()) {
				rs.next();
				array[1] = "" + rs.getInt(1);
				return array;
			}
		} catch (Exception ex) {
			throw new PersistenciaException(ex);
		}
	}

	public static MemoriaModelo criarMemoriaModelo(Connection conn, String consulta) throws PersistenciaException {
		return criarMemoriaModelo(conn, consulta, new String[0], false, new HashMap<>(), new HashMap<>());
	}

	public static MemoriaModelo criarMemoriaModelo(Connection conn, String consulta, String[] chaves,
			boolean colunaInfo, Map<String, String> mapaSequencia, Map<String, String> mapaFuncoes)
			throws PersistenciaException {
		try (PreparedStatement psmt = conn.prepareStatement(normal(consulta))) {
			try (ResultSet rs = psmt.executeQuery()) {
				ResultSetMetaData rsmd = rs.getMetaData();
				List<Coluna> colunas = criarColunas(rsmd, chaves, colunaInfo, mapaSequencia, mapaFuncoes);
				return criarMemoriaModelo(rs, colunas, colunaInfo);
			}
		} catch (Exception ex) {
			throw new PersistenciaException(ex);
		}
	}

	private static MemoriaModelo criarMemoriaModelo(ResultSet rs, List<Coluna> colunas, boolean colunaInfo)
			throws PersistenciaException {
		try {
			List<List<String>> dados = new ArrayList<>();
			int qtdColunas = colunaInfo ? colunas.size() - 1 : colunas.size();
			while (rs.next()) {
				List<String> registro = new ArrayList<>();
				for (int i = 1; i <= qtdColunas; i++) {
					String valor = colunas.get(i - 1).isBlob() ? "BLOB" : rs.getString(i);
					registro.add(valor == null ? Constantes.VAZIO : valor);
				}
				if (colunaInfo) {
					registro.add(Constantes.VAZIO);
				}
				dados.add(registro);
			}
			List<String> lista = new ArrayList<>();
			for (Coluna coluna : colunas) {
				lista.add(coluna.getNome());
			}
			return new MemoriaModelo(lista, dados);
		} catch (Exception ex) {
			throw new PersistenciaException(ex);
		}
	}

	public static PersistenciaModelo criarPersistenciaModelo(PersistenciaModelo.Parametros parametros)
			throws PersistenciaException {
		try (PreparedStatement psmt = parametros.getConn().prepareStatement(normal(parametros.getConsulta()))) {
			try (ResultSet rs = psmt.executeQuery()) {
				ResultSetMetaData rsmd = rs.getMetaData();
				List<Coluna> colunas = criarColunas(rsmd, parametros.getColunasChave(), parametros.isComColunaInfo(),
						parametros.getMapaSequencia(), parametros.getMapaFuncoes());
				return criarPersistenciaModelo(rs, colunas, parametros);
			}
		} catch (Exception ex) {
			throw new PersistenciaException(ex);
		}
	}

	private static PersistenciaModelo criarPersistenciaModelo(ResultSet rs, List<Coluna> colunas,
			PersistenciaModelo.Parametros parametros) throws PersistenciaException {
		try {
			List<List<Object>> registros = new ArrayList<>();
			int qtdColunas = parametros.isComColunaInfo() ? colunas.size() - 1 : colunas.size();
			while (rs.next()) {
				List<Object> registro = new ArrayList<>();
				for (int i = 1; i <= qtdColunas; i++) {
					Object valor = colunas.get(i - 1).isBlob() ? "BLOB" : rs.getString(i);
					registro.add(valor == null ? Constantes.VAZIO : valor);
				}
				if (parametros.isComColunaInfo()) {
					registro.add(Constantes.VAZIO);
				}
				registros.add(registro);
			}
			return new PersistenciaModelo(colunas, registros, parametros.getTabela(), parametros.getConexao());
		} catch (Exception ex) {
			throw new PersistenciaException(ex);
		}
	}

	private static List<Coluna> criarColunas(ResultSetMetaData rsmd, String[] chaves, boolean colunaInfo,
			Map<String, String> mapaSequencia, Map<String, String> mapaFuncoes) throws PersistenciaException {
		Map<String, Boolean> mapa = criarMapaTipos();
		List<Coluna> colunas = new ArrayList<>();
		if (mapaSequencia == null) {
			mapaSequencia = new HashMap<>();
		}
		if (chaves == null) {
			chaves = new String[0];
		}
		try {
			int qtdColunas = rsmd.getColumnCount();
			for (int i = 1; i <= qtdColunas; i++) {
				String tipoBanco = rsmd.getColumnTypeName(i);
				int tamanho = rsmd.getColumnDisplaySize(i);
				String classe = rsmd.getColumnClassName(i);
				String nome = rsmd.getColumnName(i).trim();
				boolean nulavel = rsmd.isNullable(i) == 1;
				boolean autoInc = rsmd.isAutoIncrement(i);
				Boolean numero = mapa.get(classe);
				int tipo = rsmd.getColumnType(i);
				boolean blob = tipo == Types.BLOB || tipo == Types.LONGVARBINARY;
				Boolean chave = false;
				if (numero == null) {
					numero = Boolean.FALSE;
				}
				for (String s : chaves) {
					if (s.trim().equalsIgnoreCase(nome)) {
						chave = Boolean.TRUE;
					}
				}
				String nomeSequencia = mapaSequencia.get(nome);
				Coluna coluna = new Coluna(nome, i - 1, numero, chave, blob, classe,
						new Coluna.Config(tamanho, tipoBanco, nulavel, false, autoInc, nomeSequencia));
				coluna.configFuncao(mapaFuncoes);
				colunas.add(coluna);
			}
			if (colunaInfo) {
				Coluna coluna = new Coluna("INFO", qtdColunas, false, false, false, "INFO",
						new Coluna.Config(0, "INFO", true, true, false, null));
				colunas.add(coluna);
			}
			return colunas;
		} catch (Exception ex) {
			throw new PersistenciaException(ex);
		}
	}

	public static List<String> listarNomeTabelas(Connection conn, Conexao conexao) throws PersistenciaException {
		try {
			List<String> resposta = new ArrayList<>();
			DatabaseMetaData m = conn.getMetaData();
			ResultSet rs = m.getTables(conexao.getCatalogo(), conexao.getEsquema(), "%",
					Preferencias.getGetObjetosBanco().split(","));
			while (rs.next()) {
				resposta.add(rs.getString(TABLE_NAME));
			}
			rs.close();
			return resposta;
		} catch (Exception ex) {
			throw new PersistenciaException(ex);
		}
	}

	private static List<String> criarLista(String... strings) {
		return Arrays.asList(strings);
	}

	public static MemoriaModelo criarModeloChavePrimaria(Connection conn, Conexao conexao, String tabela)
			throws PersistenciaException {
		try {
			List<List<String>> dados = new ArrayList<>();
			DatabaseMetaData m = conn.getMetaData();
			ResultSet rs = m.getPrimaryKeys(conexao.getCatalogo(), conexao.getEsquema(), tabela);
			while (rs.next()) {
				dados.add(criarLista(rs.getString(COLUMN_NAME), rs.getString(KEY_SEQ), rs.getString(PK_NAME)));
			}
			rs.close();
			return new MemoriaModelo(Arrays.asList(COLUMN_NAME, KEY_SEQ, PK_NAME), dados);
		} catch (Exception ex) {
			throw new PersistenciaException(ex);
		}
	}

	public static List<String> listarChavesPrimarias(Connection conn, Conexao conexao, String tabela)
			throws PersistenciaException {
		try {
			List<String> resposta = new ArrayList<>();
			DatabaseMetaData m = conn.getMetaData();
			ResultSet rs = m.getPrimaryKeys(conexao.getCatalogo(), conexao.getEsquema(), tabela);
			while (rs.next()) {
				resposta.add(rs.getString(COLUMN_NAME));
			}
			rs.close();
			return resposta;
		} catch (Exception ex) {
			throw new PersistenciaException(ex);
		}
	}

	public static List<List<String>> listarConstraints(Connection conn, Conexao conexao, String tabela)
			throws PersistenciaException {
		try {
			String consulta = String.format(conexao.getConstraint(), tabela);
			List<List<String>> resposta = new ArrayList<>();
			try (PreparedStatement psmt = conn.prepareStatement(normal(consulta))) {
				try (ResultSet rs = psmt.executeQuery()) {
					ResultSetMetaData rsmd = rs.getMetaData();
					int totalColunas = rsmd.getColumnCount();
					while (rs.next()) {
						listarConstraints(resposta, rs, totalColunas);
					}
				}
			}
			return resposta;
		} catch (Exception ex) {
			throw new PersistenciaException(ex);
		}
	}

	private static void listarConstraints(List<List<String>> resposta, ResultSet rs, int totalColunas)
			throws SQLException {
		List<String> lista = new ArrayList<>();
		for (int i = 1; i <= totalColunas; i++) {
			String s = rs.getString(i);
			if (!Util.isEmpty(s)) {
				lista.add(s);
			}
		}
		if (!lista.isEmpty()) {
			resposta.add(lista);
		}
	}

	public static List<String> listarCampos(Connection conn, Conexao conexao, String tabela)
			throws PersistenciaException {
		try {
			List<String> resposta = new ArrayList<>();
			DatabaseMetaData m = conn.getMetaData();
			ResultSet rs = m.getColumns(conexao.getCatalogo(), conexao.getEsquema(), tabela, null);
			while (rs.next()) {
				resposta.add(rs.getString(COLUMN_NAME));
			}
			rs.close();
			return resposta;
		} catch (Exception ex) {
			throw new PersistenciaException(ex);
		}
	}

	public static List<Importado> listarCamposImportados(Connection conn, Conexao conexao, String tabela)
			throws PersistenciaException {
		try {
			List<Importado> resposta = new ArrayList<>();
			DatabaseMetaData m = conn.getMetaData();
			ResultSet rs = m.getImportedKeys(conexao.getCatalogo(), conexao.getEsquema(), tabela);
			while (rs.next()) {
				String tabelaOrigem = rs.getString(PKTABLE_NAME);
				String campoOrigem = rs.getString(PKCOLUMN_NAME);
				String campo = rs.getString(FKCOLUMN_NAME);
				resposta.add(new Importado(tabelaOrigem, campoOrigem, campo));
			}
			rs.close();
			return resposta;
		} catch (Exception ex) {
			throw new PersistenciaException(ex);
		}
	}

	public static List<Exportado> listarCamposExportados(Connection conn, Conexao conexao, String tabela)
			throws PersistenciaException {
		try {
			List<Exportado> resposta = new ArrayList<>();
			DatabaseMetaData m = conn.getMetaData();
			ResultSet rs = m.getExportedKeys(conexao.getCatalogo(), conexao.getEsquema(), tabela);
			while (rs.next()) {
				String tabelaDestino = rs.getString(FKTABLE_NAME);
				String campoDestino = rs.getString(FKCOLUMN_NAME);
				String campo = rs.getString(PKCOLUMN_NAME);
				resposta.add(new Exportado(tabelaDestino, campoDestino, campo));
			}
			rs.close();
			return resposta;
		} catch (Exception ex) {
			throw new PersistenciaException(ex);
		}
	}

	public static MemoriaModelo criarModeloChavesImportadas(Connection conn, Conexao conexao, String tabela)
			throws PersistenciaException {
		try {
			List<List<String>> dados = new ArrayList<>();
			DatabaseMetaData m = conn.getMetaData();
			ResultSet rs = m.getImportedKeys(conexao.getCatalogo(), conexao.getEsquema(), tabela);
			while (rs.next()) {
				dados.add(criarLista(rs.getString(PKTABLE_NAME), rs.getString(PKCOLUMN_NAME),
						rs.getString(FKCOLUMN_NAME)));
			}
			rs.close();
			return new MemoriaModelo(Arrays.asList(PKTABLE_NAME, PKCOLUMN_NAME, FKCOLUMN_NAME), dados);
		} catch (Exception ex) {
			throw new PersistenciaException(ex);
		}
	}

	public static MemoriaModelo criarModeloChavesExportadas(Connection conn, Conexao conexao, String tabela)
			throws PersistenciaException {
		try {
			List<List<String>> dados = new ArrayList<>();
			DatabaseMetaData m = conn.getMetaData();
			ResultSet rs = m.getExportedKeys(conexao.getCatalogo(), conexao.getEsquema(), tabela);
			while (rs.next()) {
				dados.add(criarLista(rs.getString(PKCOLUMN_NAME), rs.getString(FKTABLE_NAME),
						rs.getString(FKCOLUMN_NAME)));
			}
			rs.close();
			return new MemoriaModelo(Arrays.asList(PKCOLUMN_NAME, FKTABLE_NAME, FKCOLUMN_NAME), dados);
		} catch (Exception ex) {
			throw new PersistenciaException(ex);
		}
	}

	public static MemoriaModelo criarModeloMetaDados(Connection conn, Conexao conexao, String tabela)
			throws PersistenciaException {
		String string = "SELECT * FROM " + PersistenciaModelo.prefixarEsquema(conexao, null, tabela, null)
				+ " WHERE 1 > 2";

		try (PreparedStatement psmt = conn.prepareStatement(normal(string))) {
			try (ResultSet rs = psmt.executeQuery()) {
				ResultSetMetaData rsmd = rs.getMetaData();
				int totalColunas = rsmd.getColumnCount();
				List<String> colunas = Arrays.asList("ColumnClassName", "ColumnLabel", "ColumnName", "AutoIncrement",
						"CaseSensitive", "Searchable", "Currency", "Nullable", "Signed", "ColumnDisplaySize",
						"SchemaName", "Precision", "Scale", "TableName", "CatalogName", "ColumnType", "ColumnTypeName",
						"ReadOnly", "Writable", "DefinitelyWritable");
				List<List<String>> dados = new ArrayList<>();
				final String VAZIO = Constantes.VAZIO;
				for (int i = 1; i <= totalColunas; i++) {
					List<String> linha = new ArrayList<>();
					linha.add(rsmd.getColumnClassName(i));
					linha.add(rsmd.getColumnLabel(i));
					linha.add(rsmd.getColumnName(i));
					linha.add(VAZIO + rsmd.isAutoIncrement(i));
					linha.add(VAZIO + rsmd.isCaseSensitive(i));
					linha.add(VAZIO + rsmd.isSearchable(i));
					linha.add(VAZIO + rsmd.isCurrency(i));
					linha.add(VAZIO + rsmd.isNullable(i));
					linha.add(VAZIO + rsmd.isSigned(i));
					linha.add(VAZIO + rsmd.getColumnDisplaySize(i));
					linha.add(rsmd.getSchemaName(i));
					linha.add(VAZIO + rsmd.getPrecision(i));
					linha.add(VAZIO + rsmd.getScale(i));
					linha.add(rsmd.getTableName(i));
					linha.add(rsmd.getCatalogName(i));
					linha.add(VAZIO + rsmd.getColumnType(i));
					linha.add(rsmd.getColumnTypeName(i));
					linha.add(VAZIO + rsmd.isReadOnly(i));
					linha.add(VAZIO + rsmd.isWritable(i));
					linha.add(VAZIO + rsmd.isDefinitelyWritable(i));
					dados.add(linha);
				}
				return new MemoriaModelo(colunas, dados);
			}
		} catch (Exception ex) {
			throw new PersistenciaException(ex);
		}
	}
}