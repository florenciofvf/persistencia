package br.com.persist.plugins.expressao.processador;

import java.util.Objects;

import br.com.persist.plugins.expressao.ExpressaoException;

public abstract class Instrucao {
	public static final String CIFRAO = "\\$";
	protected final String nome;
	protected String parametros;
	protected int indice;

	protected Instrucao(String nome) {
		this.nome = Objects.requireNonNull(nome);
	}

	public String getNome() {
		return nome;
	}

	public String getParametros() {
		return parametros;
	}

	public void setParametros(String parametros) {
		this.parametros = parametros;
	}

	public int getIndice() {
		return indice;
	}

	public void setIndice(int indice) {
		this.indice = indice;
	}

	public abstract Instrucao novo();

	public abstract void processar(Funcao funcao, PilhaFuncao pilhaFuncao, PilhaOperando pilhaOperando)
			throws ExpressaoException;

	protected void checarNome(String nome, Funcao funcao) throws ExpressaoException {
		if (!nome.equals(funcao.getNome())) {
			throw new ExpressaoException("erro.nomes.funcao", nome, funcao.getNome());
		}
	}

	@Override
	public String toString() {
		return nome + (parametros != null ? " " + parametros : "");
	}
}