package br.com.persist.plugins.instrucao.compilador;

import br.com.persist.plugins.instrucao.InstrucaoException;

public class FuncaoContexto extends Container {
	private final FuncaoParametrosContexto parametros;
	private final FuncaoIdentityContexto identity;
	private final CorpoContexto corpo;
	private boolean faseParametros;
	private Contexto contexto;

	public FuncaoContexto() {
		parametros = new FuncaoParametrosContexto();
		identity = new FuncaoIdentityContexto();
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

class FuncaoIdentityContexto extends AbstratoContexto {
	Token token;

	@Override
	public void identity(Compilador compilador, Token token) throws InstrucaoException {
		if (token.getString().indexOf(".") != -1) {
			compilador.invalidar(token);
		} else {
			this.token = token;
		}
	}
}

class FuncaoParametrosContexto extends Container {
	public static final IdentityOuFinalizar IDENTITY_OU_FINALIZAR = new IdentityOuFinalizar();
	public static final VirgulaOuFinalizar VIRGULA_OU_FINALIZAR = new VirgulaOuFinalizar();
	public static final Identity IDENTIT = new Identity();
	private Contexto contexto;

	public FuncaoParametrosContexto() {
		contexto = IDENTITY_OU_FINALIZAR;
	}

	@Override
	public void finalizador(Compilador compilador, Token token) throws InstrucaoException {
		contexto.finalizador(compilador, token);
		compilador.setContexto(getPai());
	}

	@Override
	public void separador(Compilador compilador, Token token) throws InstrucaoException {
		contexto.separador(compilador, token);
		contexto = IDENTIT;
	}

	@Override
	public void identity(Compilador compilador, Token token) throws InstrucaoException {
		contexto.identity(compilador, token);
		adicionar(new ParametroContexto(token));
		contexto = VIRGULA_OU_FINALIZAR;
	}
}

class IdentityOuFinalizar extends AbstratoContexto {
	Token token;

	@Override
	public void finalizador(Compilador compilador, Token token) throws InstrucaoException {
		if (!")".equals(token.getString())) {
			compilador.invalidar(token);
		}
	}

	@Override
	public void identity(Compilador compilador, Token token) throws InstrucaoException {
		if (token.getString().indexOf(".") != -1) {
			compilador.invalidar(token);
		} else {
			this.token = token;
		}
	}
}

class VirgulaOuFinalizar extends AbstratoContexto {
	@Override
	public void finalizador(Compilador compilador, Token token) throws InstrucaoException {
		if (!")".equals(token.getString())) {
			compilador.invalidar(token);
		}
	}

	@Override
	public void separador(Compilador compilador, Token token) throws InstrucaoException {
		//
	}
}

class Identity extends AbstratoContexto {
	Token token;

	@Override
	public void identity(Compilador compilador, Token token) throws InstrucaoException {
		if (token.getString().indexOf(".") != -1) {
			compilador.invalidar(token);
		} else {
			this.token = token;
		}
	}
}