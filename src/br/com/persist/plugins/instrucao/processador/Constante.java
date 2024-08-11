package br.com.persist.plugins.instrucao.processador;

import java.util.Objects;

import br.com.persist.plugins.instrucao.InstrucaoException;

public class Constante {
	private Biblioteca biblioteca;
	private InstrucaoItem cabeca;
	private InstrucaoItem cauda;
	private final String nome;
	private Object valor;

	public Constante(String nome) {
		this.nome = Objects.requireNonNull(nome);
	}

	public Biblioteca getBiblioteca() {
		return biblioteca;
	}

	public void setBiblioteca(Biblioteca biblioteca) {
		this.biblioteca = biblioteca;
	}

	public String getNome() {
		return nome;
	}

	public Object getValor() {
		return valor;
	}

	public void setValor(Object valor) {
		this.valor = valor;
	}

	public void addInstrucao(Instrucao instrucao) {
		if (instrucao == null) {
			return;
		}
		InstrucaoItem no = new InstrucaoItem(instrucao);
		if (cabeca == null) {
			cabeca = no;
		}
		if (cauda != null) {
			cauda.proximo = no;
		}
		cauda = no;
	}

	public void init() throws InstrucaoException {
		PilhaOperando pilhaOperando = new PilhaOperando();
		InstrucaoItem no = cabeca;
		while (no != null) {
			no.instrucao.processar(null, biblioteca, null, null, pilhaOperando);
			no = no.proximo;
		}
	}

	@Override
	public String toString() {
		return nome + ": " + valor;
	}
}