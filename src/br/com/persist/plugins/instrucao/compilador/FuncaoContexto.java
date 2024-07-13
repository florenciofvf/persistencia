package br.com.persist.plugins.instrucao.compilador;

import br.com.persist.plugins.instrucao.InstrucaoException;

public class FuncaoContexto extends Container {
	private final FuncaoIdentityContexto identity;
	private boolean faseParametros;

	public FuncaoContexto() {
		identity = new FuncaoIdentityContexto();
		adicionar(new ParametrosContexto());
		adicionar(new CorpoContexto());
		faseParametros = true;
		contexto = identity;
	}

	public ParametrosContexto getParametros() {
		return (ParametrosContexto) get(0);
	}

	public CorpoContexto getCorpo() {
		return (CorpoContexto) get(1);
	}

	public FuncaoIdentityContexto getIdentity() {
		return identity;
	}

	@Override
	public void inicializador(Compilador compilador, Token token) throws InstrucaoException {
		contexto.inicializador(compilador, token);
		if (faseParametros) {
			compilador.setContexto(getParametros());
			contexto = Contextos.ABRE_CHAVES;
			faseParametros = false;
		} else {
			compilador.setContexto(getCorpo());
			getCorpo().setFinalizadorPai(true);
		}
	}

	@Override
	public void finalizador(Compilador compilador, Token token) throws InstrucaoException {
		compilador.setContexto(getPai());
	}

	@Override
	public void identity(Compilador compilador, Token token) throws InstrucaoException {
		contexto.identity(compilador, token);
		contexto = Contextos.ABRE_PARENTESES;
	}

	@Override
	public String toString() {
		return "function >>> " + getParametros().toString();
	}
}