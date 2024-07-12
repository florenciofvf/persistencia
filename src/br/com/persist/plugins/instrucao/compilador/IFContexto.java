package br.com.persist.plugins.instrucao.compilador;

import br.com.persist.plugins.instrucao.InstrucaoException;
import br.com.persist.plugins.instrucao.compilador.expressao.ExpressaoContexto;

public class IFContexto extends Container {
	public static final ReservadoOuFinalizar RESERVADO_OU_FINALIZAR = new ReservadoOuFinalizar();
	private final ExpressaoContexto expressao;
	private final CorpoContexto corpo;
	private boolean faseExpressao;
	private Contexto contexto;

	public IFContexto() {
		contexto = Contextos.ABRE_PARENTESES;
		expressao = new ExpressaoContexto();
		corpo = new CorpoContexto();
		faseExpressao = true;
		adicionar(expressao);
		adicionar(corpo);
	}

	@Override
	public void inicializador(Compilador compilador, Token token) throws InstrucaoException {
		contexto.inicializador(compilador, token);
		if (faseExpressao) {
			compilador.setContexto(expressao);
			contexto = Contextos.ABRE_CHAVES;
			faseExpressao = false;
		} else {
			compilador.setContexto(corpo);
			contexto = RESERVADO_OU_FINALIZAR;
		}
	}

	@Override
	public void finalizador(Compilador compilador, Token token) throws InstrucaoException {
		contexto.finalizador(compilador, token);
		compilador.setContexto(getPai());
		normalizarArvore(compilador, token);
	}

	@Override
	public void reservado(Compilador compilador, Token token) throws InstrucaoException {
		contexto.reservado(compilador, token);
		if ("elseif".equals(token.getString())) {
			compilador.setContexto(new ElseIFContexto());
			adicionarImpl(compilador, token, (Container) compilador.getContexto());
		} else if ("else".equals(token.getString())) {
			compilador.setContexto(new ElseContexto());
			adicionarImpl(compilador, token, (Container) compilador.getContexto());
		} else {
			compilador.invalidar(token);
		}
	}

	public void adicionarImpl(Compilador compilador, Token token, Container c) throws InstrucaoException {
		Container ultimo = getUltimo();
		if (ultimo instanceof ElseContexto) {
			compilador.invalidar(token);
		}
		adicionar(c);
	}

	private void normalizarArvore(Compilador compilador, Token token) throws InstrucaoException {
		if (getSize() == 2) {
			return;
		}
		validarSequencia(compilador, token);
		normalizarArvore();
	}

	private void validarSequencia(Compilador compilador, Token token) throws InstrucaoException {
		for (int i = 2; i < getSize() - 1; i++) {
			Container c = get(i);
			if (c instanceof ElseContexto) {
				compilador.invalidar(token);
			}
		}
	}

	private void normalizarArvore() {
		/*
		 * Iterator<Container> it = getFilhos().iterator(); while (it.hasNext()) {
		 * Container c = it.next(); if (c instanceof SeparadorContexto) { it.remove(); }
		 * }
		 */
	}
}

class ReservadoOuFinalizar extends AbstratoContexto {
	private final String[] strings = { "elseif", "else" };

	@Override
	public void finalizador(Compilador compilador, Token token) throws InstrucaoException {
		if (!";".equals(token.getString())) {
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
}