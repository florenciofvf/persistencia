package br.com.persist.plugins.instrucao.compilador;

import br.com.persist.plugins.instrucao.InstrucaoException;

public class CorpoContexto extends Container {
	public static final ReservadoOuIdentityOuFinalizar RESERVADO_OU_IDENTITY_OU_FINALIZAR = new ReservadoOuIdentityOuFinalizar();
	private Contexto contexto;

	public CorpoContexto() {
		contexto = RESERVADO_OU_IDENTITY_OU_FINALIZAR;
	}

	@Override
	public void finalizador(Compilador compilador, Token token) throws InstrucaoException {
		contexto.finalizador(compilador, token);
		compilador.setContexto(getPai());
	}

	@Override
	public void reservado(Compilador compilador, Token token) throws InstrucaoException {
		contexto.finalizador(compilador, token);
		if ("const".equals(token.getString())) {
			compilador.setContexto(new ConstanteContexto());
			adicionar((Container) compilador.getContexto());
		} else if ("return".equals(token.getString())) {
			compilador.setContexto(new RetornoContexto());
			adicionar((Container) compilador.getContexto());
		} else {
			compilador.invalidar(token);
		}
	}

	@Override
	public void identity(Compilador compilador, Token token) throws InstrucaoException {
		compilador.setContexto(new InvocacaoContexto(token));
		adicionar((Container) compilador.getContexto());
	}
}

class ReservadoOuIdentityOuFinalizar extends AbstratoContexto {
	private final String[] strings = { "const", "return" };
	Token token;

	@Override
	public void finalizador(Compilador compilador, Token token) throws InstrucaoException {
		if (!"}".equals(token.getString())) {
			compilador.invalidar(token);
		}
	}

	@Override
	public void reservado(Compilador compilador, Token token) throws InstrucaoException {
		if (!igual(token.getString())) {
			compilador.invalidar(token);
		}
	}

	private boolean igual(String s) {
		for (String string : strings) {
			if (string.equals(s)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void identity(Compilador compilador, Token token) throws InstrucaoException {
		this.token = token;
	}
}