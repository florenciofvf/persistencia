package br.com.persist.plugins.instrucao.compilador.expressao;

import br.com.persist.plugins.instrucao.InstrucaoException;
import br.com.persist.plugins.instrucao.compilador.Compilador;
import br.com.persist.plugins.instrucao.compilador.Container;
import br.com.persist.plugins.instrucao.compilador.Token;

public class ExpressaoContexto extends Container {
	private final char[] modoPai;
	private boolean inicializado;

	public ExpressaoContexto(char[] modoPai) {
		this.modoPai = modoPai;
	}

	@Override
	public void inicializador(Compilador compilador, Token token) throws InstrucaoException {
		if ("(".equals(token.getString())) {
			if (inicializado) {
				ExpressaoContexto expressao = new ExpressaoContexto(null);
				expressao.inicializado = true;
				compilador.setContexto(expressao);
				adicionar((Container) compilador.getContexto());
			} else {
				inicializado = true;
			}
		} else {
			compilador.invalidar(token);
		}
	}

	@Override
	public void finalizador(Compilador compilador, Token token) throws InstrucaoException {
		if (")".equals(token.getString())) {
			montarArvore();
			compilador.setContexto(getPai());
			getPai().setModo(modoPai);
		} else {
			compilador.invalidar(token);
		}
	}

	private void montarArvore() {
	}

	@Override
	public void operador(Compilador compilador, Token token) throws InstrucaoException {
		adicionar(new OperadorContexto(token.getString()));
	}

	@Override
	public void string(Compilador compilador, Token token) throws InstrucaoException {
		adicionar(new StringContexto(token.getString()));
	}

	@Override
	public void numero(Compilador compilador, Token token) throws InstrucaoException {
		adicionar(new NumeroContexto(token.getString()));
	}

	@Override
	public void identity(Compilador compilador, Token token) throws InstrucaoException {
		adicionar(new IdentityContexto(token.getString()));
	}
}