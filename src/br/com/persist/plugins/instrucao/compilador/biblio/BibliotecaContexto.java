package br.com.persist.plugins.instrucao.compilador;

import br.com.persist.plugins.instrucao.InstrucaoException;

public class ArquivoContexto extends Container {
	@Override
	public void reservado(Compilador compilador, Token token) throws InstrucaoException {
		if ("function".equals(token.string)) {
			compilador.contexto = new FuncaoContexto();
			adicionar((Container) compilador.contexto);
		} else if ("const".equals(token.string)) {
			compilador.contexto = new ConstanteContexto();
			adicionar((Container) compilador.contexto);
		} else {
			compilador.invalidar(token);
		}
	}
}