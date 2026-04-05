package br.com.persist.plugins.expressao.processador;

import java.util.Objects;

import br.com.persist.plugins.expressao.ExpressaoException;

public abstract class Instrucao {
	protected final String nome;
	protected String parametros;
	protected int sequencia;

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

	public int getSequencia() {
		return sequencia;
	}

	public void setSequencia(int sequencia) {
		this.sequencia = sequencia;
	}

	public abstract Instrucao clonar();

	public abstract void processar(Funcao funcao, PilhaFuncao pilhaFuncao, PilhaOperando pilhaOperando)
			throws ExpressaoException;

	@Override
	public String toString() {
		return nome + (parametros != null ? " " + parametros : "");
	}
}