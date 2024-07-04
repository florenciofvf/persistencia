package br.com.persist.plugins.instrucao.compilador;

import br.com.persist.plugins.instrucao.InstrucaoException;

public class ParametrosContexto extends Container {
	private final char[] modo1 = { 'I' };
	private final char[] modo2 = { 'Y', 'F' };
	private final char[] modo3 = { 'S', 'F' };
	private final char[] modo4 = { 'Y' };

	public ParametrosContexto() {
		modo = modo1;
	}

	@Override
	public void inicializador(Compilador compilador, Token token) throws InstrucaoException {
		if (isModo('I')) {
			if ("(".equals(token.string)) {
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
			if (")".equals(token.string)) {
				compilador.contexto = getPai();
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
			if (",".equals(token.string)) {
				modo = modo4;
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
			if (token.string.indexOf(".") != -1) {
				compilador.invalidar(token);
			} else {
				// armazenar...
				modo = modo3;
			}
		} else {
			compilador.invalidar(token);
		}
	}
}