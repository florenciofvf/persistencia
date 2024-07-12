package br.com.persist.plugins.instrucao.compilador;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import br.com.persist.plugins.instrucao.InstrucaoException;
import br.com.persist.plugins.instrucao.compilador.expressao.ExpressaoContexto;

public class IFContexto extends Container {
	public static final ReservadoOuFinalizar RESERVADO_OU_FINALIZAR = new ReservadoOuFinalizar();
	private boolean faseExpressao;

	public IFContexto() {
		contexto = Contextos.ABRE_PARENTESES;
		adicionar(new ExpressaoContexto());
		adicionar(new CorpoContexto());
		faseExpressao = true;
	}

	@Override
	public void inicializador(Compilador compilador, Token token) throws InstrucaoException {
		contexto.inicializador(compilador, token);
		if (faseExpressao) {
			compilador.setContexto(get(0));
			contexto = Contextos.ABRE_CHAVES;
			faseExpressao = false;
		} else {
			compilador.setContexto(get(1));
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
		if (getSize() == 2 || (getSize() == 3 && getUltimo() instanceof ElseContexto)) {
			return;
		}
		List<Container> lista = new ArrayList<>();
		Iterator<Container> it = getFilhos().iterator();
		it.next();
		it.next();
		while (it.hasNext()) {
			Container c = it.next();
			lista.add(c);
			it.remove();
		}
		IFContexto sel = this;
		it = lista.iterator();
		while (it.hasNext()) {
			Container c = it.next();
			sel = sel.fragmentar(c, compilador, token);
		}
	}

	private IFContexto fragmentar(Container c, Compilador compilador, Token token) throws InstrucaoException {
		IFContexto resposta = new IFContexto();
		if (c instanceof ElseIFContexto) {
			ElseIFContexto elseIFContexto = (ElseIFContexto) c;
			resposta.clear();
			resposta.adicionar(elseIFContexto.getExpressao());
			resposta.adicionar(elseIFContexto.getCorpo());

			ElseContexto elseContexto = new ElseContexto();
			elseContexto.getCorpo().adicionar(resposta);
			adicionar(elseContexto);
		} else if (c instanceof ElseContexto) {
			resposta = this;
			adicionar(c);
		} else {
			compilador.invalidar(token);
		}
		return resposta;
	}

	@Override
	public String toString() {
		return "if >>> " + get(0).toString();
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