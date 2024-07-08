package br.com.persist.plugins.instrucao.compilador.funcao;

import br.com.persist.plugins.instrucao.InstrucaoException;
import br.com.persist.plugins.instrucao.compilador.Compilador;
import br.com.persist.plugins.instrucao.compilador.Container;
import br.com.persist.plugins.instrucao.compilador.Token;

public class FuncaoContexto extends Container {
	private final FuncaoParametrosContexto parametros;
	private final char[] modo1 = { 'Y' };
	private FuncaoCorpoContexto corpo;
	private final char[] modoPai;

	public FuncaoContexto(char[] modoPai) {
		parametros = new FuncaoParametrosContexto();
		this.modoPai = modoPai;
		adicionar(parametros);
		modo = modo1;
	}

	public void setCorpo(FuncaoCorpoContexto corpo) {
		if (this.corpo != null) {
			throw new IllegalArgumentException();
		}
		this.corpo = corpo;
		adicionar(corpo);
	}

	public FuncaoCorpoContexto getCorpo() {
		return corpo;
	}

	@Override
	public void finalizador(Compilador compilador, Token token) throws InstrucaoException {
		if (isModo('F')) {
			if (";".equals(token.getString())) {
				compilador.setContexto(getPai());
				getPai().setModo(modoPai);
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
			}
			compilador.setContexto(parametros);
			modo = null;
		} else {
			compilador.invalidar(token);
		}
	}
}