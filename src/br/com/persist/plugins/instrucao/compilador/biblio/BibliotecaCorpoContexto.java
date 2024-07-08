package br.com.persist.plugins.instrucao.compilador.biblio;

import br.com.persist.plugins.instrucao.InstrucaoException;
import br.com.persist.plugins.instrucao.compilador.Compilador;
import br.com.persist.plugins.instrucao.compilador.Container;
import br.com.persist.plugins.instrucao.compilador.Token;
import br.com.persist.plugins.instrucao.compilador.constante.ConstanteContexto;
import br.com.persist.plugins.instrucao.compilador.funcao.FuncaoContexto;

public class BibliotecaCorpoContexto extends Container {
	private final char[] modo1 = { 'I' };
	private final char[] modo2 = { 'R', 'F' };

	public BibliotecaCorpoContexto() {
		modo = modo1;
	}

	@Override
	public void inicializador(Compilador compilador, Token token) throws InstrucaoException {
		if (isModo('I')) {
			if ("{".equals(token.getString())) {
				modo = modo2;
			} else {
				compilador.invalidar(token);
			}
		} else {
			compilador.invalidar(token);
		}
	}

	@Override
	public void finalizador(Compilador compilador, Token token) throws InstrucaoException {
		if (isModo('F')) {
			if ("}".equals(token.getString())) {
				compilador.setContexto(getPai());
			} else {
				compilador.invalidar(token);
			}
		} else {
			compilador.invalidar(token);
		}
	}

	@Override
	public void reservado(Compilador compilador, Token token) throws InstrucaoException {
		if (isModo('R')) {
			if ("function".equals(token.getString())) {
				compilador.setContexto(new FuncaoContexto());
				adicionar((Container) compilador.getContexto());
			} else if ("const".equals(token.getString())) {
				ConstanteContexto constante = new ConstanteContexto();
				compilador.setContexto(constante.getExpressao());
				adicionar(constante);
			} else {
				compilador.invalidar(token);
			}
		} else {
			compilador.invalidar(token);
		}
	}
}