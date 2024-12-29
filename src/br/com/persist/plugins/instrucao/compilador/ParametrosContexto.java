package br.com.persist.plugins.instrucao.compilador;

import br.com.persist.plugins.instrucao.InstrucaoException;
import br.com.persist.plugins.instrucao.compilador.Token.Tipo;

public class ParametrosContexto extends Container {
	public static final IdentityOuListaOuFinalizar IDENTITY_OU_LISTA_OU_FINALIZAR = new IdentityOuListaOuFinalizar();
	public static final VirgulaOuFinalizar VIRGULA_OU_FINALIZAR = new VirgulaOuFinalizar();
	public static final Parametro PARAMETRO = new Parametro();
	private boolean finalizadorPai;

	public ParametrosContexto() {
		contexto = IDENTITY_OU_LISTA_OU_FINALIZAR;
	}

	public boolean isFinalizadorPai() {
		return finalizadorPai;
	}

	public void setFinalizadorPai(boolean finalizadorPai) {
		this.finalizadorPai = finalizadorPai;
	}

	@Override
	public void finalizador(Compilador compilador, Token token) throws InstrucaoException {
		contexto.finalizador(compilador, token);
		if (finalizadorPai) {
			getPai().finalizador(compilador, token);
		} else {
			compilador.setContexto(getPai());
		}
	}

	@Override
	public void separador(Compilador compilador, Token token) throws InstrucaoException {
		contexto.separador(compilador, token);
		contexto = PARAMETRO;
	}

	@Override
	public void identity(Compilador compilador, Token token) throws InstrucaoException {
		contexto.identity(compilador, token);
		adicionar(new ParametroContexto(token));
		compilador.tokens.add(token.novo(Tipo.PARAMETRO));
		contexto = VIRGULA_OU_FINALIZAR;
	}

	@Override
	public void lista(Compilador compilador, Token token) throws InstrucaoException {
		contexto.lista(compilador, token);
		adicionar(new ParametroContexto(token));
		contexto = VIRGULA_OU_FINALIZAR;
	}

	public boolean contem(String string) {
		return getParametro(string) != null;
	}

	public ParametroContexto getParametro(String string) {
		for (Container item : componentes) {
			ParametroContexto param = (ParametroContexto) item;
			if (param.contem(string)) {
				return param;
			}
		}
		return null;
	}

	@Override
	public String toString() {
		return "Parametro(s): " + getComponentes().toString();
	}
}

class IdentityOuListaOuFinalizar extends AbstratoContexto {
	Token token;

	@Override
	public void finalizador(Compilador compilador, Token token) throws InstrucaoException {
		if (!")".equals(token.getString())) {
			compilador.invalidar(token);
		}
	}

	@Override
	public void identity(Compilador compilador, Token token) throws InstrucaoException {
		compilador.checarTailCall(token);
		if (token.getString().indexOf(".") != -1) {
			compilador.invalidar(token);
		} else {
			this.token = token;
		}
	}

	@Override
	public void lista(Compilador compilador, Token token) throws InstrucaoException {
		if (token.getString().indexOf(":") == -1) {
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

class Parametro extends AbstratoContexto {
	Token token;

	@Override
	public void identity(Compilador compilador, Token token) throws InstrucaoException {
		if (token.getString().indexOf(".") != -1) {
			compilador.invalidar(token);
		} else {
			this.token = token;
		}
	}

	@Override
	public void lista(Compilador compilador, Token token) throws InstrucaoException {
		if (token.getString().indexOf(":") == -1) {
			compilador.invalidar(token);
		} else {
			this.token = token;
		}
	}
}