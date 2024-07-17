package br.com.persist.plugins.instrucao.processador;

import java.util.ArrayList;
import java.util.List;

import br.com.persist.plugins.instrucao.InstrucaoException;

public class PilhaFuncao {
	private final List<Funcao> funcoes;

	public PilhaFuncao() {
		funcoes = new ArrayList<>();
	}

	private void checar() throws InstrucaoException {
		if (isEmpty()) {
			throw new InstrucaoException("PilhaFuncao vazia >>> " + toString(), false);
		}
	}

	public void push(Funcao funcao) throws InstrucaoException {
		InstrucaoUtil.checarFuncao(funcao);
		funcoes.add(funcao);
	}

	public Funcao peek() throws InstrucaoException {
		checar();
		return funcoes.get(funcoes.size() - 1);
	}

	public Funcao pop() throws InstrucaoException {
		checar();
		return funcoes.remove(funcoes.size() - 1);
	}

	public int size() {
		return funcoes.size();
	}

	public boolean isEmpty() {
		return funcoes.isEmpty();
	}

	public void clear() {
		funcoes.clear();
	}

	@Override
	public String toString() {
		return "PilhaFuncao=" + funcoes.toString();
	}
}