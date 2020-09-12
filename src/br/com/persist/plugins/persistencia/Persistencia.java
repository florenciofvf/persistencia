package br.com.persist.plugins.persistencia;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.persist.plugins.conexao.Conexao;
import br.com.persist.util.Constantes;

public class Persistencia {
	private static final String FKCOLUMN_NAME = "FKCOLUMN_NAME";
	private static final String PKCOLUMN_NAME = "PKCOLUMN_NAME";
	private static final String TABLE_CATALOG = "TABLE_CATALOG";
	private static final String FKTABLE_NAME = "FKTABLE_NAME";
	private static final String PKTABLE_NAME = "PKTABLE_NAME";
	private static final String TABLE_SCHEM = "TABLE_SCHEM";
	private static final String COLUMN_NAME = "COLUMN_NAME";
	private static final String TABLE_NAME = "TABLE_NAME";

	private Persistencia() {
	}

	public static int executar(Connection conn, String sql) throws PersistenciaException {
		try (PreparedStatement psmt = conn.prepareStatement(sql)) {
			return psmt.executeUpdate();
		} catch (Exception ex) {
			throw new PersistenciaException(ex);
		}
	}

	private static Map<String, Boolean> criarMapaTipos() {
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

		return mapa;
	}

	public static PersistenciaMemoriaModelo criarModeloInfoBanco(Connection conn) throws PersistenciaException {
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

			return new PersistenciaMemoriaModelo(Arrays.asList("NOME", "VALOR"), dados);
		} catch (Exception ex) {
			throw new PersistenciaException(ex);
		}
	}

	private static List<String> criar(String nome, Object object) {
		return Arrays.asList(nome, object == null ? Constantes.VAZIO : object.toString());
	}

	public static PersistenciaMemoriaModelo criarModeloEsquema(Connection conn) throws PersistenciaException {
		try {
			List<List<String>> dados = new ArrayList<>();
			DatabaseMetaData m = conn.getMetaData();
			ResultSet rs = m.getSchemas();

			while (rs.next()) {
				dados.add(criar(rs.getString(TABLE_SCHEM), rs.getString(TABLE_CATALOG)));
			}

			rs.close();
			return new PersistenciaMemoriaModelo(Arrays.asList(TABLE_SCHEM, TABLE_CATALOG), dados);
		} catch (Exception ex) {
			throw new PersistenciaException(ex);
		}
	}

	public static int getTotalRegistros(Connection conn, String aposFROM) throws PersistenciaException {
		StringBuilder builder = new StringBuilder("SELECT COUNT(*) FROM " + aposFROM);
		try (PreparedStatement psmt = conn.prepareStatement(builder.toString())) {
			try (ResultSet rs = psmt.executeQuery()) {
				rs.next();
				return rs.getInt(1);
			}
		} catch (Exception ex) {
			throw new PersistenciaException(ex);
		}
	}

	public static PersistenciaMemoriaModelo criarPersistenciaMemoriaModelo(Connection conn, String consulta,
			String[] chaves, boolean colunaInfo, Map<String, String> mapaSequencia) throws PersistenciaException {
		try (PreparedStatement psmt = conn.prepareStatement(consulta)) {
			try (ResultSet rs = psmt.executeQuery()) {
				ResultSetMetaData rsmd = rs.getMetaData();
				List<Coluna> colunas = criarColunas(rsmd, chaves, colunaInfo, mapaSequencia);
				return criarPersistenciaMemoriaModelo(rs, colunas, colunaInfo);
			}
		} catch (Exception ex) {
			throw new PersistenciaException(ex);
		}
	}

	private static PersistenciaMemoriaModelo criarPersistenciaMemoriaModelo(ResultSet rs, List<Coluna> colunas,
			boolean colunaInfo) throws PersistenciaException {
		try {
			List<List<String>> dados = new ArrayList<>();
			int qtdColunas = colunas.size();

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

			return new PersistenciaMemoriaModelo(lista, dados);
		} catch (Exception ex) {
			throw new PersistenciaException(ex);
		}
	}

	private static List<Coluna> criarColunas(ResultSetMetaData rsmd, String[] chaves, boolean colunaInfo,
			Map<String, String> mapaSequencia) throws PersistenciaException {
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

				String nomeSequencia = mapaSequencia.get(nome.toLowerCase());
				Coluna coluna = new Coluna(nome, i - 1, numero, chave, blob, classe,
						new Coluna.Config(tamanho, tipoBanco, nulavel, false, autoInc, nomeSequencia));
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
			ResultSet rs = m.getTables(conexao.getCatalogo(), conexao.getEsquema(), "%", new String[] { "TABLE" });

			while (rs.next()) {
				resposta.add(rs.getString(TABLE_NAME));
			}

			rs.close();
			return resposta;
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
}