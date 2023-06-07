package br.com.persist.plugins.instrucao.cmpl;

import java.util.ArrayList;
import java.util.List;

import br.com.persist.plugins.instrucao.InstrucaoException;

public class PilhaNo {
	private final List<No> nos;

	public PilhaNo() {
		nos = new ArrayList<>();
	}

	private void checar() throws InstrucaoException {
		if (isEmpty()) {
			throw new InstrucaoException("Pilha vazia >>> " + toString(), false);
		}
	}

	public No ref() throws InstrucaoException {
		checar();
		return nos.get(nos.size() - 1);
	}

	public void add(No no) throws InstrucaoException {
		if (no == null) {
			throw new InstrucaoException("Null para pilha >>> " + toString(), false);
		}
		No ativado = ref();
		ativado.add(no);
		if (ativado instanceof Infixa) {
			Infixa infixa = (Infixa) ativado;
			if (infixa.valido()) {
				pop();
			}
		}
	}

	public void push(No no) {
		if (no != null) {
			nos.add(no);
		}
	}

	public No pop() throws InstrucaoException {
		checar();
		return nos.remove(nos.size() - 1);
	}

	public int size() {
		return nos.size();
	}

	public boolean isEmpty() {
		return size() == 0;
	}

	@Override
	public String toString() {
		return nos.toString();
	}
}