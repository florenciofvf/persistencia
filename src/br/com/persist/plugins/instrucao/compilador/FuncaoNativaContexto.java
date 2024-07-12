package br.com.persist.plugins.instrucao.compilador;

import br.com.persist.plugins.instrucao.InstrucaoException;

public class FuncaoNativaContexto extends Container {
	private final FuncaoIdentityContexto identityBiblio;
	private final FuncaoIdentityContexto identity;
	private boolean faseBiblio;

	public FuncaoNativaContexto() {
		identityBiblio = new FuncaoIdentityContexto();
		identity = new FuncaoIdentityContexto();
		adicionar(new ParametrosContexto());
		contexto = identityBiblio;
		faseBiblio = true;
	}

	public FuncaoIdentityContexto getIdentityBiblio() {
		return identityBiblio;
	}

	public FuncaoIdentityContexto getIdentity() {
		return identity;
	}

	@Override
	public void inicializador(Compilador compilador, Token token) throws InstrucaoException {
		contexto.inicializador(compilador, token);
		contexto = Contextos.PONTO_VIRGULA;
		compilador.setContexto(get(0));
	}

	@Override
	public void finalizador(Compilador compilador, Token token) throws InstrucaoException {
		contexto.finalizador(compilador, token);
		compilador.setContexto(getPai());
	}

	@Override
	public void identity(Compilador compilador, Token token) throws InstrucaoException {
		contexto.identity(compilador, token);
		if (faseBiblio) {
			contexto = identity;
			faseBiblio = false;
		} else {
			contexto = Contextos.ABRE_PARENTESES;
		}
	}

	@Override
	public String toString() {
		return "function_native >>> " + get(0).toString();
	}
}