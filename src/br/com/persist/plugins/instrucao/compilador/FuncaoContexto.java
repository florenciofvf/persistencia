package br.com.persist.plugins.instrucao.compilador;

import br.com.persist.plugins.instrucao.InstrucaoException;

public class FuncaoContexto extends Container {
	private final FuncaoIdentityContexto identity;
	private final ParametrosContexto parametros;
	private final CorpoContexto corpo;
	private boolean faseParametros;
	private Contexto contexto;

	public FuncaoContexto() {
		identity = new FuncaoIdentityContexto();
		parametros = new ParametrosContexto();
		corpo = new CorpoContexto();
		faseParametros = true;
		adicionar(parametros);
		contexto = identity;
		adicionar(corpo);
	}

	public FuncaoIdentityContexto getIdentity() {
		return identity;
	}

	@Override
	public void inicializador(Compilador compilador, Token token) throws InstrucaoException {
		contexto.inicializador(compilador, token);
		if (faseParametros) {
			compilador.setContexto(parametros);
			contexto = Contextos.ABRE_CHAVES;
			faseParametros = false;
		} else {
			compilador.setContexto(corpo);
			contexto = Contextos.PONTO_VIRGULA;
		}
	}

	@Override
	public void finalizador(Compilador compilador, Token token) throws InstrucaoException {
		contexto.finalizador(compilador, token);
		compilador.setContexto(getPai());
	}

	@Override
	public void identity(Compilador compilador, Token token) throws InstrucaoException {
		contexto.identity(compilador, token);
		contexto = Contextos.ABRE_PARENTESES;
	}
}