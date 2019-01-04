package br.com.persist.banco;

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

import br.com.persist.Objeto;
import br.com.persist.modelo.ListagemModelo;
import br.com.persist.modelo.RegistroModelo;
import br.com.persist.tabela.Coluna;
import br.com.persist.util.Util;

public class Persistencia {

	public static int executar(String sql, Connection conn) throws Exception {
		PreparedStatement psmt = conn.prepareStatement(sql);
		int i = psmt.executeUpdate();
		psmt.close();
		return i;
	}

	public static int getTotalRegistros(Connection conn, Objeto objeto, String complemento, Conexao conexao)
			throws Exception {
		StringBuilder builder = new StringBuilder("SELECT COUNT(*) FROM " + objeto.getTabela(conexao.getEsquema()));
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

	public static RegistroModelo criarModeloRegistro(Connection conn, String consulta, String[] chaves, Objeto objeto,
			Conexao conexao) throws Exception {
		PreparedStatement psmt = conn.prepareStatement(consulta);

		ResultSet rs = psmt.executeQuery();
		RegistroModelo modelo = criarModelo(rs, chaves, objeto.getTabela(conexao.getEsquema()));

		rs.close();
		psmt.close();

		return modelo;
	}

	private static RegistroModelo criarModelo(ResultSet rs, String[] chaves, String tabela) throws Exception {
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
			String tipoBanco = rsmd.getColumnTypeName(i);
			int tamanho = rsmd.getColumnDisplaySize(i);
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

			Coluna coluna = new Coluna(nome, i - 1, numero, chave, tipo == Types.BLOB || tipo == Types.LONGVARBINARY,
					classe, tamanho, tipoBanco);
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

		return new RegistroModelo(colunas, registros, tabela);
	}

	public static ListagemModelo criarModeloInfoBanco(Connection conn) throws Exception {
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

		return new ListagemModelo(Arrays.asList("NOME", "VALOR"), dados);
	}

	private static List<String> criar(String nome, Object object) {
		return Arrays.asList(nome, object == null ? "" : object.toString());
	}

	private static List<String> criar(String... strings) {
		return Arrays.asList(strings);
	}

	public static ListagemModelo criarModeloEsquema(Connection conn) throws Exception {
		List<List<String>> dados = new ArrayList<>();
		DatabaseMetaData m = conn.getMetaData();

		ResultSet rs = m.getSchemas();

		while (rs.next()) {
			dados.add(criar(rs.getString("TABLE_SCHEM"), rs.getString("TABLE_CATALOG")));
		}

		rs.close();

		return new ListagemModelo(Arrays.asList("TABLE_SCHEM", "TABLE_CATALOG"), dados);
	}

	public static ListagemModelo criarModeloChavePrimaria(Connection conn, Objeto objeto, Conexao conexao)
			throws Exception {
		List<List<String>> dados = new ArrayList<>();
		DatabaseMetaData m = conn.getMetaData();

		ResultSet rs = m.getPrimaryKeys(null, conexao.getEsquema(), objeto.getTabela2());

		while (rs.next()) {
			dados.add(criar(rs.getString("COLUMN_NAME"), rs.getString("KEY_SEQ"), rs.getString("PK_NAME")));
		}

		rs.close();

		return new ListagemModelo(Arrays.asList("COLUMN_NAME", "KEY_SEQ", "PK_NAME"), dados);
	}

	public static ListagemModelo criarModeloChavesExportadas(Connection conn, Objeto objeto, Conexao conexao)
			throws Exception {
		List<List<String>> dados = new ArrayList<>();
		DatabaseMetaData m = conn.getMetaData();

		ResultSet rs = m.getExportedKeys(null, conexao.getEsquema(), objeto.getTabela2());

		while (rs.next()) {
			dados.add(
					criar(rs.getString("PKCOLUMN_NAME"), rs.getString("FKTABLE_NAME"), rs.getString("FKCOLUMN_NAME")));
		}

		rs.close();

		return new ListagemModelo(Arrays.asList("PKCOLUMN_NAME", "FKTABLE_NAME", "FKCOLUMN_NAME"), dados);
	}

	public static ListagemModelo criarModeloChavesImportadas(Connection conn, Objeto objeto, Conexao conexao)
			throws Exception {
		List<List<String>> dados = new ArrayList<>();
		DatabaseMetaData m = conn.getMetaData();

		ResultSet rs = m.getImportedKeys(null, conexao.getEsquema(), objeto.getTabela2());

		while (rs.next()) {
			dados.add(
					criar(rs.getString("PKTABLE_NAME"), rs.getString("PKCOLUMN_NAME"), rs.getString("FKCOLUMN_NAME")));
		}

		rs.close();

		return new ListagemModelo(Arrays.asList("PKTABLE_NAME", "PKCOLUMN_NAME", "FKCOLUMN_NAME"), dados);
	}

	public static ListagemModelo criarModeloMetaDados(Connection conn, Objeto objeto, Conexao conexao)
			throws Exception {
		StringBuilder builder = new StringBuilder(
				"SELECT * FROM " + objeto.getTabela(conexao.getEsquema()) + " WHERE 1 > 2");
		PreparedStatement psmt = conn.prepareStatement(builder.toString());
		ResultSet rs = psmt.executeQuery();
		ResultSetMetaData rsmd = rs.getMetaData();
		int totalColunas = rsmd.getColumnCount();

		List<String> colunas = Arrays.asList("ColumnClassName", "ColumnLabel", "ColumnName", "AutoIncrement",
				"CaseSensitive", "Searchable", "Currency", "Nullable", "Signed", "ColumnDisplaySize", "SchemaName",
				"Precision", "Scale", "TableName", "CatalogName", "ColumnType", "ColumnTypeName", "ReadOnly",
				"Writable", "DefinitelyWritable");
		List<List<String>> dados = new ArrayList<>();

		for (int i = 1; i <= totalColunas; i++) {
			List<String> linha = new ArrayList<>();

			linha.add(rsmd.getColumnClassName(i));
			linha.add(rsmd.getColumnLabel(i));
			linha.add(rsmd.getColumnName(i));
			linha.add("" + rsmd.isAutoIncrement(i));
			linha.add("" + rsmd.isCaseSensitive(i));
			linha.add("" + rsmd.isSearchable(i));
			linha.add("" + rsmd.isCurrency(i));
			linha.add("" + rsmd.isNullable(i));
			linha.add("" + rsmd.isSigned(i));
			linha.add("" + rsmd.getColumnDisplaySize(i));
			linha.add(rsmd.getSchemaName(i));
			linha.add("" + rsmd.getPrecision(i));
			linha.add("" + rsmd.getScale(i));
			linha.add(rsmd.getTableName(i));
			linha.add(rsmd.getCatalogName(i));
			linha.add("" + rsmd.getColumnType(i));
			linha.add(rsmd.getColumnTypeName(i));
			linha.add("" + rsmd.isReadOnly(i));
			linha.add("" + rsmd.isWritable(i));
			linha.add("" + rsmd.isDefinitelyWritable(i));

			dados.add(linha);
		}

		rs.close();
		psmt.close();

		return new ListagemModelo(colunas, dados);
	}
}