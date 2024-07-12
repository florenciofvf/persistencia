package br.com.persist.plugins.instrucao.compilador;

import br.com.persist.plugins.instrucao.InstrucaoException;

public class FuncaoIdentityContexto extends AbstratoContexto {
	Token token;

	@Override
	public void identity(Compilador compilador, Token token) throws InstrucaoException {
		if (token.getString().indexOf(".") != -1) {
			compilador.invalidar(token);
		} else {
			this.token = token;
		}
	}
}