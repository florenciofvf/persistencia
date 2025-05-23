package br.com.persist.plugins.instrucao.compilador;

import java.io.PrintWriter;
import java.util.Iterator;

import br.com.persist.plugins.instrucao.InstrucaoException;

public class ExpressaoContexto extends ListaMapaContexto {
	private ExpressaoContextoListener listener;

	public ExpressaoContexto(ExpressaoContextoListener listener) {
		contexto = Contextos.PARENTESES;
		this.listener = listener;
	}

	public ExpressaoContexto() {
		this(null);
	}

	public ExpressaoContextoListener getListener() {
		return listener;
	}

	public void setListener(ExpressaoContextoListener listener) {
		this.listener = listener;
	}

	@Override
	public void inicializador(Compilador compilador, Token token) throws InstrucaoException {
		contexto.inicializador(compilador, token);
		if (getUltimo() instanceof IdentityContexto) {
			IdentityContexto ultimo = (IdentityContexto) excluirUltimo();
			compilador.setContexto(new ArgumentoContexto(ultimo));
		} else {
			compilador.setContexto(new ExpressaoContexto());
		}
		adicionarImpl(compilador, token, (Container) compilador.getContexto());
	}

	@Override
	public void finalizador(Compilador compilador, Token token) throws InstrucaoException {
		if (listener != null) {
			montarArvore(compilador);
			listener.finalizador(compilador, token, this);
		} else {
			contexto.finalizador(compilador, token);
			compilador.setContexto(getPai());
			montarArvore(compilador);
		}
	}

	@Override
	public void separador(Compilador compilador, Token token) throws InstrucaoException {
		if (listener == null) {
			super.separador(compilador, token);
		} else {
			listener.separador(compilador, token, this);
		}
	}

	@Override
	public void operador(Compilador compilador, Token token) throws InstrucaoException {
		if (getSize() == 1 && getPrimeiro() instanceof OperadorContexto) {
			compilador.invalidar(token);
		}
		adicionarImpl(compilador, token, new OperadorContexto(token));
	}

	@Override
	protected void adicionarImpl(Compilador compilador, Token token, Container c) throws InstrucaoException {
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
		if (size == 1 && getPrimeiro() instanceof OperadorContexto) {
			return true;
		}
		if (size > 1) {
			return (get(size - 2) instanceof OperadorContexto) && (getUltimo() instanceof OperadorContexto);
		}
		return false;
	}

	private boolean validoMergear(Container c) {
		if (c instanceof StringContexto) {
			return false;
		}
		if (getUltimo() instanceof OperadorContexto) {
			OperadorContexto operador = (OperadorContexto) getUltimo();
			if ("-".equals(operador.getId()) || "+".equals(operador.getId())) {
				return !(c instanceof OperadorContexto);
			}
		}
		return false;
	}

	private void montarArvore(Compilador compilador) throws InstrucaoException {
		if (getSize() == 0) {
			return;
		}
		if (getPrimeiro() instanceof OperadorContexto) {
			OperadorContexto operador = (OperadorContexto) getPrimeiro();
			compilador.invalidar(operador.getToken());
		} else if (getUltimo() instanceof OperadorContexto) {
			OperadorContexto operador = (OperadorContexto) getUltimo();
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

	private void montarArvore() throws InstrucaoException {
		Iterator<Container> it = getComponentes().iterator();
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
			throw new InstrucaoException("erro.expressao_invalida");
		}

		adicionar(sel);
	}

	@Override
	public void indexar(Indexador indexador) {
		sequencia = indexador.value();
		super.indexar(indexador);
		indexarNegativo(indexador);
	}

	@Override
	public void salvar(Compilador compilador, PrintWriter pw) throws InstrucaoException {
		super.salvar(compilador, pw);
		salvarNegativo(compilador, pw);
	}

	@Override
	public String toString() {
		return "Expressao: " + getComponentes().toString();
	}
}