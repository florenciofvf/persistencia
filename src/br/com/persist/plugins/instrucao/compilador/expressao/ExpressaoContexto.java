package br.com.persist.plugins.instrucao.compilador.expressao;

import java.util.Iterator;

import br.com.persist.plugins.instrucao.InstrucaoException;
import br.com.persist.plugins.instrucao.compilador.Compilador;
import br.com.persist.plugins.instrucao.compilador.Container;
import br.com.persist.plugins.instrucao.compilador.Contexto;
import br.com.persist.plugins.instrucao.compilador.Contextos;
import br.com.persist.plugins.instrucao.compilador.Token;

public class ExpressaoContexto extends Container {
	private Contexto contexto;

	public ExpressaoContexto() {
		contexto = Contextos.PARENTESES;
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
		montarArvore(compilador);
	}

	@Override
	public void operador(Compilador compilador, Token token) throws InstrucaoException {
		if (getSize() == 1 && get(0) instanceof OperadorContexto) {
			compilador.invalidar(token);
		}
		adicionarImpl(compilador, token, new OperadorContexto(token));
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

	private void montarArvore(Compilador compilador) throws InstrucaoException {
		if (getSize() == 0) {
			return;
		}
		int size = getSize();
		if (get(0) instanceof OperadorContexto) {
			OperadorContexto operador = (OperadorContexto) get(0);
			compilador.invalidar(operador.getToken());
		} else if (get(size - 1) instanceof OperadorContexto) {
			OperadorContexto operador = (OperadorContexto) get(size - 1);
			compilador.invalidar(operador.getToken());
		}
		validarSequencia(compilador);
		montarArvore();
	}

	private void validarSequencia(Compilador compilador) throws InstrucaoException {
		for (int i = 0; i < getSize(); i++) {
			Container c = get(i);
			if (i % 2 == 0) {
				if (c instanceof OperadorContexto) {
					OperadorContexto operador = (OperadorContexto) c;
					compilador.invalidar(operador.getToken());
				}
			} else {
				if (!(c instanceof OperadorContexto)) {
					OperadorContexto operador = (OperadorContexto) c;
					compilador.invalidar(operador.getToken());
				}
			}
		}
	}

	private void montarArvore() {
		Iterator<Container> it = getFilhos().iterator();
		Container sel = it.next();
		it.remove();

		if (it.hasNext()) {
			OperadorContexto operador = (OperadorContexto) it.next();
			it.remove();
			operador.adicionar(sel);
			Container c = it.next();
			it.remove();
			operador.adicionar(c);
			sel = operador;
		}

		while (it.hasNext()) {
			OperadorContexto operador = (OperadorContexto) it.next();
			it.remove();

			OperadorContexto selecionado = (OperadorContexto) sel;
			if (operador.possuoPrioridadeSobre(selecionado)) {
				Container ultimo = selecionado.excluirUltimo();
				operador.adicionar(ultimo);
				selecionado.adicionar(operador);
				Container c = it.next();
				it.remove();
				operador.adicionar(c);
			} else {
				operador.adicionar(selecionado);
				Container c = it.next();
				it.remove();
				operador.adicionar(c);
				sel = operador;
			}
		}

		if (getSize() != 0 || sel == null) {
			throw new IllegalStateException();
		}

		adicionar(sel);
	}

	public void adicionarImpl(Compilador compilador, Token token, Container c) throws InstrucaoException {
		Container ult = getUltimo();
		if (ult != null && !(ult instanceof OperadorContexto) && !(c instanceof OperadorContexto)) {
			compilador.invalidar(token);
		}
		if (candidatoMergear()) {
			if (validoMergear(c)) {
				Container ultimo = excluirUltimo();
				c.negativar(ultimo);
				adicionar(c);
			} else {
				compilador.invalidar(token);
			}
		} else {
			adicionar(c);
		}
	}

	private boolean candidatoMergear() {
		int size = getSize();
		if (size == 1 && get(0) instanceof OperadorContexto) {
			return true;
		}
		if (size > 1) {
			return (get(size - 2) instanceof OperadorContexto) && (get(size - 1) instanceof OperadorContexto);
		}
		return false;
	}

	private boolean validoMergear(Container c) {
		int size = getSize();
		if (c instanceof StringContexto) {
			return false;
		}
		if (get(size - 1) instanceof OperadorContexto) {
			OperadorContexto operador = (OperadorContexto) get(size - 1);
			if ("-".equals(operador.getId()) || "+".equals(operador.getId())) {
				return !(c instanceof OperadorContexto);
			}
		}
		return false;
	}
}