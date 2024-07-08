package br.com.persist.plugins.instrucao.compilador.funcao;

import br.com.persist.plugins.instrucao.InstrucaoException;
import br.com.persist.plugins.instrucao.compilador.Compilador;
import br.com.persist.plugins.instrucao.compilador.Container;
import br.com.persist.plugins.instrucao.compilador.Token;
import br.com.persist.plugins.instrucao.compilador.constante.ConstanteContexto;
import br.com.persist.plugins.instrucao.compilador.invocacao.InvocacaoContexto;

public class FuncaoCorpoContexto extends Container {
	private final char[] modoPai;
	private final char[] modo1 = { 'I' };
	private final char[] modo2 = { 'R', 'Y', 'F' };

	public FuncaoCorpoContexto(char[] modoPai) {
		this.modoPai = modoPai;
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
				getPai().setModo(modoPai);
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
			if ("const".equals(token.getString())) {
				ConstanteContexto constante = new ConstanteContexto(modo2);
				compilador.setContexto(constante.getExpressao());
				adicionar(constante);
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
			InvocacaoContexto invocacao = new InvocacaoContexto(modo2, token.getString());
			compilador.setContexto(invocacao.getArgumento());
			adicionar(invocacao);
			modo = null;
		} else {
			compilador.invalidar(token);
		}
	}
}