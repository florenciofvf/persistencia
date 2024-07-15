package br.com.persist.plugins.instrucao.compilador;

import br.com.persist.plugins.instrucao.InstrucaoException;

public class ParametrosContexto extends Container {
	public static final IdentityOuFinalizar IDENTITY_OU_FINALIZAR = new IdentityOuFinalizar();
	public static final VirgulaOuFinalizar VIRGULA_OU_FINALIZAR = new VirgulaOuFinalizar();
	public static final ParametroIdentity PARAMETRO_IDENTITY = new ParametroIdentity();

	public ParametrosContexto() {
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
		contexto = PARAMETRO_IDENTITY;
	}

	@Override
	public void identity(Compilador compilador, Token token) throws InstrucaoException {
		contexto.identity(compilador, token);
		adicionar(new ParametroContexto(token));
		contexto = VIRGULA_OU_FINALIZAR;
	}

	@Override
	public String toString() {
		return "Parametro(s): " + getComponentes().toString();
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

class ParametroIdentity extends AbstratoContexto {
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