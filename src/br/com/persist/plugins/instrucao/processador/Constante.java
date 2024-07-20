package br.com.persist.plugins.instrucao.processador;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import br.com.persist.plugins.instrucao.InstrucaoException;

public class Constante {
	private final List<Instrucao> instrucoes;
	private Biblioteca biblioteca;
	private final String nome;
	private Object valor;
	private int indice;

	public Constante(String nome) {
		this.nome = Objects.requireNonNull(nome);
		instrucoes = new ArrayList<>();
	}

	public Biblioteca getBiblioteca() {
		return biblioteca;
	}

	public void setBiblioteca(Biblioteca biblioteca) {
		this.biblioteca = biblioteca;
	}

	public int getIndice() {
		return indice;
	}

	public void setIndice(int indice) throws InstrucaoException {
		if (indice < 0 || indice >= instrucoes.size()) {
			throw new InstrucaoException("erro.constante_set_indice", nome);
		}
		this.indice = indice;
	}

	public String getNome() {
		return nome;
	}

	public List<Instrucao> getInstrucoes() {
		return instrucoes;
	}

	public Object getValor() {
		return valor;
	}

	public void setValor(Object valor) {
		this.valor = valor;
	}

	Instrucao getInstrucao() {
		Instrucao resp = instrucoes.get(indice);
		indice++;
		return resp;
	}

	public void addInstrucao(Instrucao instrucao) {
		if (instrucao != null) {
			instrucoes.add(instrucao);
		}
	}

	public void init() throws InstrucaoException {
		PilhaOperando pilhaOperando = new PilhaOperando();
		for (Instrucao instrucao : instrucoes) {
			instrucao.processar(null, biblioteca, null, null, pilhaOperando);
		}
	}

	@Override
	public String toString() {
		return nome + ": " + valor;
	}
}