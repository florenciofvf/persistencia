package br.com.persist.plugins.instrucao.pro;

import java.util.Objects;

import br.com.persist.plugins.instrucao.InstrucaoException;

public abstract class Instrucao {
	protected final Metodo metodo;
	protected final String nome;

	protected Instrucao(Metodo metodo, String nome) {
		this.nome = Objects.requireNonNull(nome);
		this.metodo = metodo;
	}

	public String getNome() {
		return nome;
	}

	public abstract Instrucao clonar(Metodo metodo);

	public abstract void executar(PilhaMetodo pilhaMetodo, PilhaOperando pilhaOperando, CacheBiblioteca cacheBiblioteca)
			throws InstrucaoException;

	public void setParam(String string) throws InstrucaoException {
	}

	public String getParam() {
		return null;
	}

	@Override
	public String toString() {
		String param = getParam();
		return nome + (param != null ? " " + param : "");
	}
}