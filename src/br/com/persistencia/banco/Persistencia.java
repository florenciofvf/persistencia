package br.com.persistencia.banco;

import java.sql.Connection;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;

import br.com.persistencia.Arg;
import br.com.persistencia.Objeto;
//import br.com.arvore.modelo.ModeloRegistro;
import br.com.persistencia.util.Constantes;

public class Persistencia {

//	public static int excluirObjetos(Objeto objeto) throws Exception {
//		if (Constantes.INFLAR_DESATIVADO) {
//			return 0;
//		}
//
//		Connection conn = Conexao.getConnection();
//		PreparedStatement psmt = criarPreparedStatement(conn, objeto, objeto.getGrupoDelete());
//
//		int i = psmt.executeUpdate();
//		psmt.close();
//		return i;
//	}

//	public static int inserirObjeto(Objeto objeto) throws Exception {
//		if (Constantes.INFLAR_DESATIVADO) {
//			return 0;
//		}
//
//		Connection conn = Conexao.getConnection();
//		PreparedStatement psmt = criarPreparedStatement(conn, objeto, objeto.getGrupoInsert());
//
//		int i = psmt.executeUpdate();
//		psmt.close();
//		return i;
//	}

//	public static int atualizarObjetos(Objeto objeto) throws Exception {
//		if (Constantes.INFLAR_DESATIVADO) {
//			return 0;
//		}
//
//		Connection conn = Conexao.getConnection();
//		PreparedStatement psmt = criarPreparedStatement(conn, objeto, objeto.getGrupoUpdate());
//
//		int i = psmt.executeUpdate();
//		psmt.close();
//		return i;
//	}

	public static List<Objeto> getObjetos(Objeto objeto) throws Exception {
		if (Constantes.INFLAR_DESATIVADO) {
			return new ArrayList<>();
		}

		Connection conn = Conexao.getConnection();
		PreparedStatement psmt = criarPreparedStatement(conn, objeto, objeto.getGrupoItensArvore());

		ResultSet rs = psmt.executeQuery();
		List<Objeto> objetos = coletar(rs);

		rs.close();
		psmt.close();

		return objetos;
	}

	private static PreparedStatement criarPreparedStatement(Connection conn, Objeto objeto, String instrucao)
			throws Exception {
		PreparedStatement psmt = null;

		if (Constantes.ESTRATEGIA_PARAMS == Constantes.ESTRATEGIA_PSMT_META) {
			psmt = conn.prepareStatement(instrucao);
			parametrosMeta(objeto.getPai(), psmt);

		} else if (Constantes.ESTRATEGIA_PARAMS == Constantes.ESTRATEGIA_PSMT_SET) {
			psmt = conn.prepareStatement(instrucao);
			parametrosSet(instrucao, objeto.getPai(), psmt);

		} else if (Constantes.ESTRATEGIA_PARAMS == Constantes.ESTRATEGIA_SUBSTITUIR) {
			psmt = conn.prepareStatement(PersistenciaUtil.substituir(instrucao, objeto.getPai()));

		}

		return psmt;
	}

	private static void parametrosMeta(Objeto objetoArgs, PreparedStatement psmt) throws Exception {
		ParameterMetaData pmd = psmt.getParameterMetaData();

		int parametros = pmd.getParameterCount();

		for (int i = 1; i <= parametros; i++) {
			Arg arg = objetoArgs.getArgs()[i - 1];
			arg.set(psmt, i);
		}
	}

	private static void parametrosSet(String instrucao, Objeto objetoArgs, PreparedStatement psmt) throws Exception {
		int[] parametros = PersistenciaUtil.getIndiceParametros(instrucao);

		for (int i = 1; i <= parametros.length; i++) {
			Arg arg = objetoArgs.getArgs()[i - 1];
			arg.set(psmt, i);
		}
	}

	private static List<Objeto> coletar(ResultSet rs) throws Exception {
		ResultSetMetaData rsmd = rs.getMetaData();
		List<Objeto> objetos = new ArrayList<>();
		int colunas = rsmd.getColumnCount();

		while (rs.next()) {
			Objeto objeto = new Objeto(rs.getString(1));

			Arg[] args = new Arg[colunas - 1];

			for (int i = 2; i <= colunas; i++) {
				Arg arg = Arg.criar(rsmd.getColumnLabel(i), rsmd.getColumnName(i));
				arg.get(rs, i);
				args[i - 2] = arg;
			}

			objeto.setArgs(args);
			objetos.add(objeto);
		}

		return objetos;
	}

//	public static ModeloRegistro criarModeloRegistro(Objeto objeto) throws Exception {
//		if (Constantes.INFLAR_DESATIVADO) {
//			return ModeloRegistro.criarModeloRegistroVazio();
//		}
//
//		Connection conn = Conexao.getConnection();
//		PreparedStatement psmt = criarPreparedStatement(conn, objeto, objeto.getGrupoItensTabela());
//
//		ResultSet rs = psmt.executeQuery();
//		ModeloRegistro modelo = criarModelo(rs);
//
//		rs.close();
//		psmt.close();
//
//		return modelo;
//	}

//	private static ModeloRegistro criarModelo(ResultSet rs) throws Exception {
//		Map<String, Boolean> mapa = new HashMap<>();
//		mapa.put("java.math.BigDecimal", Boolean.TRUE);
//		mapa.put("java.math.BigInteger", Boolean.TRUE);
//		mapa.put("java.lang.Character", Boolean.FALSE);
//		mapa.put("java.lang.Boolean", Boolean.FALSE);
//		mapa.put("java.lang.Integer", Boolean.TRUE);
//		mapa.put("java.lang.String", Boolean.FALSE);
//		mapa.put("java.lang.Double", Boolean.TRUE);
//		mapa.put("java.lang.Float", Boolean.TRUE);
//		mapa.put("java.lang.Short", Boolean.TRUE);
//		mapa.put("java.lang.Long", Boolean.TRUE);
//		mapa.put("java.lang.Byte", Boolean.TRUE);
//
//		ResultSetMetaData rsmd = rs.getMetaData();
//		int qtdColunas = rsmd.getColumnCount();
//
//		List<String> colunas = new ArrayList<>();
//		colunas.add(Constantes.LINHA);
//
//		for (int i = 1; i <= qtdColunas; i++) {
//			colunas.add(rsmd.getColumnName(i));
//		}
//
//		boolean[] colunasNumero = new boolean[qtdColunas + 1];
//		colunasNumero[0] = mapa.get("java.lang.Integer");
//
//		for (int i = 1; i <= qtdColunas; i++) {
//			String classe = rsmd.getColumnClassName(i);
//			colunasNumero[i] = mapa.get(classe) == null ? Boolean.FALSE : mapa.get(classe);
//		}
//
//		List<List<String>> registros = new ArrayList<>();
//		int linha = 1;
//
//		while (rs.next()) {
//			List<String> registro = new ArrayList<>();
//			registro.add("" + linha);
//			linha++;
//
//			for (int i = 1; i <= qtdColunas; i++) {
//				registro.add(rs.getString(i) == null ? "" : rs.getString(i));
//			}
//
//			registros.add(registro);
//		}
//
//		ModeloRegistro modelo = new ModeloRegistro(colunas, registros);
//		modelo.setColunasNumero(colunasNumero);
//
//		return modelo;
//	}
}