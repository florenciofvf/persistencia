package br.com.persist.plugins.instrucao.compilador;

import br.com.persist.plugins.instrucao.InstrucaoException;
import br.com.persist.plugins.instrucao.compilador.expressao.ArgumentoContexto;

public class InvocacaoContexto extends Container {
	private final ArgumentoContexto argumento;
	private Contexto contexto;

	public InvocacaoContexto(Token token) {
		contexto = Contextos.ABRE_PARENTESES;
		argumento = new ArgumentoContexto(null);
		adicionar(argumento);
		this.token = token;
	}

	@Override
	public void inicializador(Compilador compilador, Token token) throws InstrucaoException {
		contexto.inicializador(compilador, token);
		compilador.setContexto(argumento);
		contexto = Contextos.PONTO_VIRGULA;
	}

	@Override
	public void finalizador(Compilador compilador, Token token) throws InstrucaoException {
		contexto.finalizador(compilador, token);
		compilador.setContexto(getPai());
	}

	@Override
	public String toString() {
		return "invocacao >>> " + argumento.toString();
	}
}