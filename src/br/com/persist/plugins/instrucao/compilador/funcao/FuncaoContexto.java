package br.com.persist.plugins.instrucao.compilador;

import br.com.persist.plugins.instrucao.InstrucaoException;

public class FuncaoContexto extends Container {
	@Override
	public void identity(Compilador compilador, Token token) throws InstrucaoException {
		if (token.string.indexOf(".") != -1) {
			compilador.invalidar(token);
		}
		compilador.contexto = new ParametrosContexto();
		adicionar((Container) compilador.contexto);
	}
}