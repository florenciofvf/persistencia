package br.com.persist.plugins.instrucao.compilador.expressao;

import java.util.Iterator;

import br.com.persist.plugins.instrucao.InstrucaoException;
import br.com.persist.plugins.instrucao.compilador.Compilador;
import br.com.persist.plugins.instrucao.compilador.Container;
import br.com.persist.plugins.instrucao.compilador.Token;
import br.com.persist.plugins.instrucao.compilador.invocacao.InvocarContexto;

public class ExpressaoContexto extends Container {
	private final char[] modoPai;
	private boolean inicializado;

	public ExpressaoContexto(char[] modoPai) {
		this.modoPai = modoPai;
	}

	@Override
	public void inicializador(Compilador compilador, Token token) throws InstrucaoException {
		if ("(".equals(token.getString())) {
			if (inicializado) {
				if (getUltimo() instanceof IdentityContexto) {
					Container ultimo = excluirUltimo();
					InvocarContexto invocar = new InvocarContexto(ultimo);
					invocar.setInicializado(true);
					compilador.setContexto(invocar);
				} else {
					ExpressaoContexto expressao = new ExpressaoContexto(null);
					expressao.inicializado = true;
					compilador.setContexto(expressao);
				}
				adicionarImpl(compilador, token, (Container) compilador.getContexto());
			} else {
				inicializado = true;
			}
		} else {
			compilador.invalidar(token);
		}
	}

	@Override
	public void finalizador(Compilador compilador, Token token) throws InstrucaoException {
		if (")".equals(token.getString())) {
			montarArvore(compilador);
			compilador.setContexto(getPai());
			getPai().setModo(modoPai);
		} else {
			compilador.invalidar(token);
		}
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
		montar();
	}

	private void montar() {
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