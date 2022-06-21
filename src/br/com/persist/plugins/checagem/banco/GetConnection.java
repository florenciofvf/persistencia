package br.com.persist.plugins.checagem.banco;

import java.sql.DriverManager;

import br.com.persist.plugins.checagem.ChecagemException;
import br.com.persist.plugins.checagem.Contexto;
import br.com.persist.plugins.checagem.FuncaoBinariaOuMaior;

public class GetConnection extends FuncaoBinariaOuMaior {
	private static final String ERRO = "Erro GetConnection";

	public GetConnection() {
		super(4);
	}

	@Override
	public Object executar(Contexto ctx) throws ChecagemException {
		Object op0 = param0().executar(ctx);
		Object op1 = param1().executar(ctx);
		Object op2 = parametros.get(2).executar(ctx);
		Object op3 = parametros.get(3).executar(ctx);
		checkObrigatorioString(op0, ERRO + " >>> op0");
		checkObrigatorioString(op1, ERRO + " >>> op1");
		checkObrigatorioString(op2, ERRO + " >>> op2");
		checkObrigatorioString(op3, ERRO + " >>> op3");
		String driver = (String) op0;
		String url = (String) op1;
		String usuario = (String) op2;
		String senha = (String) op3;
		try {
			Class.forName(driver);
			return DriverManager.getConnection(url, usuario, senha);
		} catch (Exception ex) {
			throw new ChecagemException(ERRO + " >>> " + ex.getMessage());
		}
	}
}