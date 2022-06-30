package br.com.persist.plugins.checagem.banco;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import br.com.persist.assistencia.Util;
import br.com.persist.plugins.checagem.Bloco;
import br.com.persist.plugins.checagem.Checagem;
import br.com.persist.plugins.checagem.ChecagemException;
import br.com.persist.plugins.checagem.Contexto;
import br.com.persist.plugins.checagem.FuncaoBinariaOuNParam;

public class Select extends FuncaoBinariaOuNParam {
	private static final String ERRO = "Erro Select";

	@Override
	public Object executar(Checagem checagem, Bloco bloco, Contexto ctx) throws ChecagemException {
		List<Object> resposta = new ArrayList<>();
		Object op0 = param0().executar(checagem, bloco, ctx);
		Object op1 = param1().executar(checagem, bloco, ctx);
		checkObrigatorioString(op0, ERRO + " >>> op0");
		checkObrigatorioString(op1, ERRO + " >>> op1");
		Object conn = ctx.get((String) op0);
		if (!(conn instanceof Connection)) {
			throw new ChecagemException(ERRO + " >>> Conexao invalida");
		}
		@SuppressWarnings("resource")
		Connection connection = (Connection) conn;
		String instrucao = (String) op1;
		try (Statement st = connection.createStatement()) {
			for (int i = 2; i < parametros.size(); i += 2) {
				Object nomeParametro = parametros.get(i).executar(checagem, bloco, ctx);
				checkObrigatorioString(nomeParametro, ERRO + " >>> op" + i);
				int indiceValor = i + 1;
				if (indiceValor >= parametros.size()) {
					throw new ChecagemException("Parametro sem valor >>> " + nomeParametro);
				}
				Object valorParametro = parametros.get(indiceValor).executar(checagem, bloco, ctx);
				instrucao = substituirParametro(instrucao, (String) nomeParametro, valorParametro);
			}
			try (ResultSet rs = st.executeQuery(instrucao)) {
				while (rs.next()) {
					resposta.add(rs.getObject(1));
				}
			}
		} catch (SQLException ex) {
			throw new ChecagemException(ERRO + " >>> " + ex.getMessage());
		}
		return resposta;
	}

	private String substituirParametro(String instrucao, String nomeParametro, Object valorParametro) {
		String normalizado = normalizar(valorParametro);
		return Util.replaceAll(instrucao, nomeParametro, normalizado);
	}

	private String normalizar(Object valorParametro) {
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
}