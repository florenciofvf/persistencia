package br.com.persist.plugins.instrucao;

import java.util.ArrayList;
import java.util.List;

public class PilhaMetodo {
	private final List<Metodo> metodos;

	public PilhaMetodo() {
		metodos = new ArrayList<>();
	}

	private void checar() throws InstrucaoException {
		if (isEmpty()) {
			throw new InstrucaoException("PilhaMetodo vazia >>> " + toString(), false);
		}
	}

	public Metodo peek() throws InstrucaoException {
		checar();
		return metodos.get(metodos.size() - 1);
	}

	public void push(Metodo metodo) throws InstrucaoException {
		InstrucaoUtil.checarMetodo(metodo);
		metodos.add(metodo);
	}

	public Metodo pop() throws InstrucaoException {
		checar();
		return metodos.remove(metodos.size() - 1);
	}

	public int size() {
		return metodos.size();
	}

	public boolean isEmpty() {
		return size() == 0;
	}

	@Override
	public String toString() {
		return metodos.toString();
	}
}