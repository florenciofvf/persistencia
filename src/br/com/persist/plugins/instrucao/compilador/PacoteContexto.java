package br.com.persist.plugins.instrucao.compilador;

import java.io.PrintWriter;

import br.com.persist.plugins.instrucao.InstrucaoConstantes;
import br.com.persist.plugins.instrucao.InstrucaoException;

public class PacoteContexto extends Container {
	private String string;

	public PacoteContexto() {
		contexto = Contextos.TEXTO;
	}

	@Override
	public void finalizador(Compilador compilador, Token token) throws InstrucaoException {
		contexto.finalizador(compilador, token);
		compilador.setContexto(getPai());
	}

	@Override
	public void string(Compilador compilador, Token token) throws InstrucaoException {
		contexto.string(compilador, token);
		string = token.toString();
		contexto = Contextos.PONTO_VIRGULA;
	}

	@Override
	public void salvar(Compilador compilador, PrintWriter pw) throws InstrucaoException {
		pw.println(InstrucaoConstantes.PREFIXO_PACKAGE + string);
	}

	@Override
	public String toString() {
		return string;
	}
}