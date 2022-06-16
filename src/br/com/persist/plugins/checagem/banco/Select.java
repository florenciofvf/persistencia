package br.com.persist.plugins.checagem.banco;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import br.com.persist.plugins.checagem.ChecagemException;
import br.com.persist.plugins.checagem.Contexto;
import br.com.persist.plugins.checagem.FuncaoBinariaOuNParam;

public class Select extends FuncaoBinariaOuNParam {
	private static final String ERRO = "Erro Select";

	@Override
	public Object executar(Contexto ctx) throws ChecagemException {
		List<Object> resposta = new ArrayList<>();
		Object op0 = param0().executar(ctx);
		Object op1 = param1().executar(ctx);
		checkObrigatorioString(op0, ERRO + " >>> op0");
		checkObrigatorioString(op1, ERRO + " >>> op1");
		Object conn = ctx.get((String) op0);
		if (!(conn instanceof Connection)) {
			throw new ChecagemException(ERRO + " >>> Conexao invalida");
		}
		@SuppressWarnings("resource")
		Connection connection = (Connection) conn;
		try (PreparedStatement psmt = connection.prepareStatement((String) op1)) {
			for (int i = 2; i < parametros.size(); i++) {
				Object valor = parametros.get(i).executar(ctx);
				psmt.setObject(i - 1, valor);
			}
			try (ResultSet rs = psmt.executeQuery()) {
				while (rs.next()) {
					resposta.add(rs.getObject(1));
				}
			}
		} catch (SQLException ex) {
			throw new ChecagemException(ERRO + ex.getMessage());
		}
		return resposta;
	}
}