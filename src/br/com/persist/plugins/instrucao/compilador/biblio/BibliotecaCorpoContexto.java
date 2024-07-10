package br.com.persist.plugins.instrucao.compilador.biblio;

import br.com.persist.plugins.instrucao.InstrucaoException;
import br.com.persist.plugins.instrucao.compilador.Compilador;
import br.com.persist.plugins.instrucao.compilador.Container;
import br.com.persist.plugins.instrucao.compilador.Token;
import br.com.persist.plugins.instrucao.compilador.constante.ConstanteContexto;
import br.com.persist.plugins.instrucao.compilador.funcao.FuncaoContexto;

public class BibliotecaCorpoContexto extends Container {
	@Override
	public void finalizador(Compilador compilador, Token token) throws InstrucaoException {
		if ("}".equals(token.getString())) {
			compilador.setContexto(getPai());
			getPai().setFinalizado(true);
		} else {
			compilador.invalidar(token);
		}
	}

	@Override
	public void reservado(Compilador compilador, Token token) throws InstrucaoException {
		if ("function".equals(token.getString())) {
			compilador.setContexto(new FuncaoContexto());
			adicionar((Container) compilador.getContexto());
		} else if ("const".equals(token.getString())) {
			compilador.setContexto(new ConstanteContexto());
			adicionar((Container) compilador.getContexto());
		} else {
			compilador.invalidar(token);
		}
	}
}