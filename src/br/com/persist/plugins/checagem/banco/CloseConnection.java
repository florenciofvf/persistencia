package br.com.persist.plugins.checagem.banco;

import java.sql.Connection;
import java.sql.SQLException;

import br.com.persist.plugins.checagem.ChecagemException;
import br.com.persist.plugins.checagem.Contexto;
import br.com.persist.plugins.checagem.FuncaoUnaria;

public class CloseConnection extends FuncaoUnaria {
	private static final String ERRO = "Erro CloseConnection";

	@Override
	public Object executar(Contexto ctx) throws ChecagemException {
		Object op0 = param0().executar(ctx);
		checkObrigatorioString(op0, ERRO + " >>> op0");
		String pri = (String) op0;
		Object obj = ctx.get(pri);
		if (obj instanceof Connection) {
			Connection conn = (Connection) obj;
			try {
				conn.close();
				return Boolean.TRUE;
			} catch (SQLException e) {
				return Boolean.FALSE;
			}
		}
		return Boolean.FALSE;
	}
}