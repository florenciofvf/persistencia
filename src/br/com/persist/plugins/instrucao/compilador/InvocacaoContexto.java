package br.com.persist.plugins.instrucao.compilador;

import br.com.persist.plugins.instrucao.InstrucaoException;

public class InvocacaoContexto extends Container {
	public InvocacaoContexto(Token token) {
		adicionar(new ArgumentoContexto(null));
		contexto = Contextos.ABRE_PARENTESES;
		this.token = token;
	}

	public ArgumentoContexto getArgumento() {
		return (ArgumentoContexto) get(0);
	}

	@Override
	public void inicializador(Compilador compilador, Token token) throws InstrucaoException {
		contexto.inicializador(compilador, token);
		compilador.setContexto(getArgumento());
		contexto = Contextos.PONTO_VIRGULA;
	}

	@Override
	public void finalizador(Compilador compilador, Token token) throws InstrucaoException {
		contexto.finalizador(compilador, token);
		compilador.setContexto(getPai());
	}

	@Override
	public String toString() {
		return "invocacao >>> " + getArgumento().toString();
	}
}