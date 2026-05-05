package br.com.persist.plugins.expressao.processador;

import br.com.persist.plugins.expressao.ExpressaoException;

public abstract class Instrucao {
	public static final String CIFRAO = "\\$";
	protected final String nome;
	protected final int indice;

	protected Instrucao(int indice, String nome) throws ExpressaoException {
		if (indice < 0) {
			throw new ExpressaoException("\u00CDndice negativo (Instru\u00E7\u00E3o)");
		}
		if (nome == null || nome.trim().isEmpty()) {
			throw new ExpressaoException("Nome de instru\u00E7\u00E3o inv\u00E1lido");
		}
		this.indice = indice;
		this.nome = nome;
	}

	public String getNome() {
		return nome;
	}

	public int getIndice() {
		return indice;
	}

	protected String get(String[] array) {
		StringBuilder builder = new StringBuilder();
		for (String item : array) {
			if (builder.length() > 0) {
				builder.append("$");
			}
			builder.append(item);
		}
		return builder.toString();
	}

	public abstract void processar(Funcao funcao, PilhaFuncao pilhaFuncao, PilhaOperando pilhaOperando)
			throws ExpressaoException;

	protected void checarNome(String nome, Funcao funcao) throws ExpressaoException {
		if (!nome.equals(funcao.getNome())) {
			throw new ExpressaoException("erro.nomes.funcao", nome, funcao.getNome());
		}
	}

	@Override
	public String toString() {
		return indice + ": " + nome;
	}
}