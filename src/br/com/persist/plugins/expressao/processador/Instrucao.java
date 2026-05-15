package br.com.persist.plugins.expressao.processador;

import br.com.persist.plugins.expressao.ExpressaoConstantes;
import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.ExpressaoUtil;

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

	protected String get(String[] array, String string) {
		StringBuilder builder = new StringBuilder();
		for (String item : array) {
			if (builder.length() > 0) {
				builder.append("$");
			}
			builder.append(item);
		}
		builder.append(".");
		builder.append(string);
		return builder.toString();
	}

	protected void log(String string, PilhaOperando pilhaOperando) {
		if (ExpressaoConstantes.DEBUG_INSTRUCAO) {
			string = ExpressaoUtil.completar(string);
			ExpressaoUtil.print(string, pilhaOperando);
		}
	}

	public abstract void processar(Funcao funcao, PilhaFuncao pilhaFuncao, PilhaOperando pilhaOperando)
			throws ExpressaoException;

	protected Funcao getFuncaoAlvo(Funcao origem, String[] nomeFuncoes) throws ExpressaoException {
		if (origem == null) {
			throw new ExpressaoException("getFuncaoAlvo(): Origem null.", false);
		}
		if (nomeFuncoes == null) {
			throw new ExpressaoException("getFuncaoAlvo(): nomeFuncoes null.", false);
		}
		if (nomeFuncoes.length == 0) {
			throw new ExpressaoException("getFuncaoAlvo(): nomeFuncoes length == 0.", false);
		}
		int i = 0;
		String nomeFuncao = nomeFuncoes[i];
		Funcao resposta = getFuncao(origem, nomeFuncao);
		i++;
		while (i < nomeFuncoes.length) {
			nomeFuncao = nomeFuncoes[i];
			resposta = getFuncao(resposta.getParent(), nomeFuncao);
			i++;
		}
		return resposta;
	}

	private Funcao getFuncao(Funcao origem, String nome) throws ExpressaoException {
		Funcao funcao = origem;
		while (funcao != null) {
			if (nome.equals(funcao.getNome())) {
				return funcao;
			}
			funcao = funcao.getParent();
		}
		throw new ExpressaoException("getFuncaoAlvo(): Funcao inexistente na hierarquia: " + nome, false);
	}

	@Override
	public String toString() {
		return "[" + indice + ": " + nome + "]";
	}
}