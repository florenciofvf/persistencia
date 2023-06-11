package br.com.persist.plugins.instrucao;

import java.util.Objects;

import br.com.persist.plugins.instrucao.pro.CacheBiblioteca;
import br.com.persist.plugins.instrucao.pro.Metodo;
import br.com.persist.plugins.instrucao.pro.PilhaMetodo;
import br.com.persist.plugins.instrucao.pro.PilhaOperando;

public abstract class Instrucao {
	protected final Metodo metodo;
	protected final String nome;

	public Instrucao(Metodo metodo, String nome) {
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
}