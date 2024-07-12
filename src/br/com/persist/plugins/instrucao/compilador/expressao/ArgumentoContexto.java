package br.com.persist.plugins.instrucao.compilador.expressao;

import java.util.Iterator;

import br.com.persist.plugins.instrucao.InstrucaoException;
import br.com.persist.plugins.instrucao.compilador.Compilador;
import br.com.persist.plugins.instrucao.compilador.Container;
import br.com.persist.plugins.instrucao.compilador.Contexto;
import br.com.persist.plugins.instrucao.compilador.Contextos;
import br.com.persist.plugins.instrucao.compilador.Token;

public class ArgumentoContexto extends Container {
	private final Container container;
	private Contexto contexto;

	public ArgumentoContexto(Container container) {
		contexto = Contextos.PARENTESES;
		this.container = container;
	}

	public Container getContainer() {
		return container;
	}

	@Override
	public void inicializador(Compilador compilador, Token token) throws InstrucaoException {
		contexto.inicializador(compilador, token);
		if (getUltimo() instanceof IdentityContexto) {
			Container ultimo = excluirUltimo();
			compilador.setContexto(new ArgumentoContexto(ultimo));
		} else {
			compilador.setContexto(new ExpressaoContexto());
		}
		adicionarImpl(compilador, token, (Container) compilador.getContexto());
	}

	@Override
	public void finalizador(Compilador compilador, Token token) throws InstrucaoException {
		contexto.finalizador(compilador, token);
		compilador.setContexto(getPai());
		normalizarArvore(compilador);
	}

	@Override
	public void separador(Compilador compilador, Token token) throws InstrucaoException {
		if (isEmpty() || getUltimo() instanceof SeparadorContexto) {
			compilador.invalidar(token);
		}
		adicionarImpl(compilador, token, new SeparadorContexto(token));
	}

	@Override
	public void string(Compilador compilador, Token token) throws InstrucaoException {
		adicionarImpl(compilador, token, new StringContexto(token));
	}

	@Override
	public void numero(Compilador compilador, Token token) throws InstrucaoException {
		adicionarImpl(compilador, token, new NumeroContexto(token));
	}

	@Override
	public void identity(Compilador compilador, Token token) throws InstrucaoException {
		adicionarImpl(compilador, token, new IdentityContexto(token));
	}

	public void adicionarImpl(Compilador compilador, Token token, Container c) throws InstrucaoException {
		Container ult = getUltimo();
		if (ult != null && !(ult instanceof SeparadorContexto) && !(c instanceof SeparadorContexto)) {
			compilador.invalidar(token);
		}
		adicionar(c);
	}

	private void normalizarArvore(Compilador compilador) throws InstrucaoException {
		if (getSize() == 0) {
			return;
		}
		int size = getSize();
		if (get(0) instanceof SeparadorContexto) {
			SeparadorContexto separador = (SeparadorContexto) get(0);
			compilador.invalidar(separador.getToken());
		} else if (get(size - 1) instanceof SeparadorContexto) {
			SeparadorContexto separador = (SeparadorContexto) get(size - 1);
			compilador.invalidar(separador.getToken());
		}
		validarSequencia(compilador);
		normalizarArvore();
	}

	private void validarSequencia(Compilador compilador) throws InstrucaoException {
		for (int i = 0; i < getSize(); i++) {
			Container c = get(i);
			if (i % 2 == 0) {
				if (c instanceof SeparadorContexto) {
					SeparadorContexto separador = (SeparadorContexto) c;
					compilador.invalidar(separador.getToken());
				}
			} else {
				if (!(c instanceof SeparadorContexto)) {
					SeparadorContexto separador = (SeparadorContexto) c;
					compilador.invalidar(separador.getToken());
				}
			}
		}
	}

	private void normalizarArvore() {
		Iterator<Container> it = getFilhos().iterator();
		while (it.hasNext()) {
			Container c = it.next();
			if (c instanceof SeparadorContexto) {
				it.remove();
			}
		}
	}
}