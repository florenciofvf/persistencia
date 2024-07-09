package br.com.persist.plugins.instrucao.compilador.constante;

import br.com.persist.plugins.instrucao.InstrucaoException;
import br.com.persist.plugins.instrucao.compilador.Compilador;
import br.com.persist.plugins.instrucao.compilador.Container;
import br.com.persist.plugins.instrucao.compilador.Token;
import br.com.persist.plugins.instrucao.compilador.expressao.ExpressaoContexto;
import br.com.persist.plugins.instrucao.compilador.funcao.ParametroContexto;

public class ConstanteExpressaoContexto extends Container {
	private final char[] modo1 = { 'I' };
	private final char[] modo2 = { 'Y' };
	private final char[] modo3 = { 'S' };
	private final char[] modo4 = { 'F' };

	public ConstanteExpressaoContexto() {
		modo = modo1;
	}

	@Override
	public void inicializador(Compilador compilador, Token token) throws InstrucaoException {
		if (isModo('I')) {
			if ("(".equals(token.getString())) {
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
			if (")".equals(token.getString())) {
				compilador.setContexto(getPai());
			} else {
				compilador.invalidar(token);
			}
		} else {
			compilador.invalidar(token);
		}
	}

	@Override
	public void separador(Compilador compilador, Token token) throws InstrucaoException {
		if (isModo('S')) {
			if (",".equals(token.getString())) {
				compilador.setContexto(new ExpressaoContexto(modo4));
				adicionar((Container) compilador.getContexto());
				modo = null;
			} else {
				compilador.invalidar(token);
			}
		} else {
			compilador.invalidar(token);
		}
	}

	@Override
	public void identity(Compilador compilador, Token token) throws InstrucaoException {
		if (isModo('Y')) {
			if (token.getString().indexOf(".") != -1) {
				compilador.invalidar(token);
			} else {
				adicionar(new ParametroContexto(token.getString()));
				modo = modo3;
			}
		} else {
			compilador.invalidar(token);
		}
	}
}