package br.com.persist.plugins.instrucao.compilador;

import br.com.persist.plugins.instrucao.InstrucaoException;

public class CorpoContexto extends Container {
	public static final ReservadoOuIdentityOuFinalizar RESERVADO_OU_IDENTITY_OU_FINALIZAR = new ReservadoOuIdentityOuFinalizar();
	private GotoContexto gotoContexto = new GotoContexto();
	private boolean finalizadorPai;

	public CorpoContexto() {
		contexto = RESERVADO_OU_IDENTITY_OU_FINALIZAR;
	}

	public boolean isFinalizadorPai() {
		return finalizadorPai;
	}

	public void setFinalizadorPai(boolean finalizadorPai) {
		this.finalizadorPai = finalizadorPai;
	}

	@Override
	public void inicializador(Compilador compilador, Token token) throws InstrucaoException {
		if ("[".equals(token.getString())) {
			compilador.setContexto(new ColcheteContexto(false));
			adicionarImpl(compilador, token, (Container) compilador.getContexto());
		} else {
			compilador.invalidar(token);
		}
	}

	@Override
	public void finalizador(Compilador compilador, Token token) throws InstrucaoException {
		contexto.finalizador(compilador, token);
		if (isEmpty()) {
			compilador.invalidar(token, "Corpo vazio!");
		}
		if (finalizadorPai) {
			getPai().finalizador(compilador, token);
		} else {
			compilador.setContexto(getPai());
		}
	}

	@Override
	public void reservado(Compilador compilador, Token token) throws InstrucaoException {
		contexto.reservado(compilador, token);
		if ("const".equals(token.getString())) {
			compilador.setContexto(new ConstanteContexto());
			adicionarImpl(compilador, token, (Container) compilador.getContexto());
		} else if ("return".equals(token.getString())) {
			compilador.setContexto(new RetornoContexto());
			adicionarImpl(compilador, token, (Container) compilador.getContexto());
		} else if ("if".equals(token.getString())) {
			compilador.setContexto(new IFContexto());
			adicionarImpl(compilador, token, (Container) compilador.getContexto());
		} else if ("while".equals(token.getString())) {
			compilador.setContexto(new WhileContexto());
			adicionarImpl(compilador, token, (Container) compilador.getContexto());
		} else {
			compilador.invalidar(token);
		}
	}

	@Override
	public void identity(Compilador compilador, Token token) throws InstrucaoException {
		compilador.setContexto(new InvocacaoContexto(token));
		adicionarImpl(compilador, token, (Container) compilador.getContexto());
	}

	public void adicionarImpl(Compilador compilador, Token token, Container c) throws InstrucaoException {
		Container ultimo = getUltimo();
		if (ultimo instanceof RetornoContexto) {
			compilador.invalidar(token);
		}
		adicionar(c);
	}

	@Override
	public void estruturarImpl() {
		if (pai instanceof IFContexto) {
			IFContexto ifContexto = (IFContexto) pai;
			if (ifContexto.isMinimo()) {
				return;
			}
			if (!especial()) {
				adicionar(gotoContexto);
			}
		} else if (pai instanceof WhileContexto && !especial2()) {
			adicionar(gotoContexto);
		}
	}

	private boolean especial() {
		Container c = getUltimo();
		return c instanceof IFContexto || c instanceof RetornoContexto;
	}

	private boolean especial2() {
		Container c = getUltimo();
		return c instanceof RetornoContexto;
	}

	protected Container getContainerApos(Container c) {
		int indice = getIndice(c);
		if (indice == getSize() - 1) {
			return null;
		}
		return get(indice + 1);
	}

	@Override
	public String toString() {
		return "Corpo: " + getComponentes().toString();
	}
}

class ReservadoOuIdentityOuFinalizar extends AbstratoContexto {
	private final String[] strings = { "const", "return", "if", "while" };
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