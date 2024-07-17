package br.com.persist.plugins.instrucao.processador;

import java.util.Objects;

import br.com.persist.plugins.instrucao.InstrucaoException;

public abstract class Instrucao {
	protected final String nome;
	protected String parametros;
	protected Funcao funcao;

	protected Instrucao(String nome) {
		this.nome = Objects.requireNonNull(nome);
	}

	public String getNome() {
		return nome;
	}

	public Funcao getFuncao() {
		return funcao;
	}

	public void setFuncao(Funcao funcao) {
		this.funcao = funcao;
	}

	public String getParametros() {
		return parametros;
	}

	public void setParametros(String parametros) {
		this.parametros = parametros;
	}

	public abstract Instrucao clonar();

	public abstract void processar(CacheBiblioteca cacheBiblioteca, PilhaFuncao pilhaMetodo, PilhaOperando pilhaOperando) throws InstrucaoException;

	@Override
	public String toString() {
		return nome + (parametros != null ? " " + parametros : "");
	}
}