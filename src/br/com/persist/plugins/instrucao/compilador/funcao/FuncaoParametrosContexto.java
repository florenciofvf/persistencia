package br.com.persist.plugins.instrucao.compilador.funcao;

import br.com.persist.plugins.instrucao.InstrucaoException;
import br.com.persist.plugins.instrucao.compilador.Compilador;
import br.com.persist.plugins.instrucao.compilador.Container;
import br.com.persist.plugins.instrucao.compilador.Token;

public class FuncaoParametrosContexto extends Container {
	private final char[] modo1 = { 'I' };
	private final char[] modo2 = { 'Y', 'F' };
	private final char[] modo3 = { 'S', 'F' };
	private final char[] modo4 = { 'Y' };

	public FuncaoParametrosContexto() {
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
				FuncaoContexto funcao = (FuncaoContexto) getPai();
				funcao.setCorpo(new FuncaoCorpoContexto(new char[] { 'F' }));
				compilador.setContexto(funcao.getCorpo());
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