package br.com.persist.plugins.checagem.banco;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import br.com.persist.assistencia.ListaEncadeada;
import br.com.persist.assistencia.Util;
import br.com.persist.plugins.checagem.Bloco;
import br.com.persist.plugins.checagem.Checagem;
import br.com.persist.plugins.checagem.ChecagemException;
import br.com.persist.plugins.checagem.Contexto;
import br.com.persist.plugins.checagem.funcao.FuncaoBinariaOuNParam;

public class Select extends FuncaoBinariaOuNParam {
	private static final String ERRO = "Erro Select";

	@Override
	public Object executar(Checagem checagem, Bloco bloco, Contexto ctx) throws ChecagemException {
		ListaEncadeada<Object> resposta = new ListaEncadeada<>();
		Object op0 = param0().executar(checagem, bloco, ctx);
		Object op1 = param1().executar(checagem, bloco, ctx);
		checkObrigatorioString(op1, ERRO + " >>> op1");
		if (!(op0 instanceof Connection)) {
			throw new ChecagemException(getClass(), ERRO + " >>> Nao eh conexao valida >>> op0");
		}
		@SuppressWarnings("resource")
		Connection connection = (Connection) op0;
		String instrucao = (String) op1;
		try (Statement st = connection.createStatement()) {
			for (int i = 2; i < parametros.size(); i += 2) {
				Object nomeParametro = parametros.get(i).executar(checagem, bloco, ctx);
				checkObrigatorioString(nomeParametro, ERRO + " >>> op" + i);
				int indiceValor = i + 1;
				if (indiceValor >= parametros.size()) {
					throw new ChecagemException(getClass(), "Parametro sem valor >>> " + nomeParametro);
				}
				Object valorParametro = parametros.get(indiceValor).executar(checagem, bloco, ctx);
				instrucao = substituirParametro(instrucao, (String) nomeParametro, valorParametro);
			}
			processar(resposta, instrucao, st);
		} catch (SQLException ex) {
			throw new ChecagemException(getClass(), ERRO + " >>> " + ex.getMessage());
		}
		return resposta;
	}

	private void processar(ListaEncadeada<Object> resposta, String instrucao, Statement st) throws SQLException {
		try (ResultSet rs = st.executeQuery(instrucao)) {
			ResultSetMetaData rsmd = rs.getMetaData();
			int qtdColunas = rsmd.getColumnCount();
			if (qtdColunas == 1) {
				while (rs.next()) {
					resposta.add(rs.getObject(1));
				}
			} else {
				while (rs.next()) {
					Map<String, Object> map = new HashMap<>();
					for (int i = 1; i <= qtdColunas; i++) {
						String nome = rsmd.getColumnName(i).trim();
						Object valor = rs.getObject(i);
						map.put(nome, valor);
					}
					resposta.add(map);
				}
			}
		}
	}

	static String substituirParametro(String instrucao, String nomeParametro, Object valorParametro) {
		String normalizado = normalizar(valorParametro);
		return Util.replaceAll(instrucao, nomeParametro, normalizado);
	}

	private static String normalizar(Object valorParametro) {
		if (valorParametro instanceof CharSequence || valorParametro instanceof Character
				|| valorParametro instanceof Date) {
			return "'" + valorParametro.toString() + "'";
		} else if (valorParametro instanceof Number) {
			return valorParametro.toString();
		} else if (valorParametro instanceof Collection<?>) {
			StringBuilder sb = new StringBuilder();
			Collection<?> colecao = (Collection<?>) valorParametro;
			for (Object object : colecao) {
				if (sb.length() > 0) {
					sb.append(", ");
				}
				sb.append(normalizar(object));
			}
			return sb.toString();
		} else if (valorParametro != null) {
			return valorParametro.toString();
		}
		return "''";
	}

	@Override
	public String getDoc() throws ChecagemException {
		return "select(conn, instrucao, [param, valor, paramN, valorN])";
	}
}