package br.com.persist.plugins.expressao.invocacao;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.processador.Funcao;
import br.com.persist.plugins.expressao.processador.Instrucao;
import br.com.persist.plugins.expressao.processador.PilhaFuncao;

public abstract class Invoke extends Instrucao {
	protected Invoke(int indice, String nome) throws ExpressaoException {
		super(indice, nome);
	}

	protected static boolean isVoid(String string) {
		return "void".equals(string) || "java.lang.Void".equals(string);
	}

	protected String stringPilhaMetodo(Funcao funcao, PilhaFuncao pilhaMetodo) throws ExpressaoException {
		StringBuilder sb = new StringBuilder(funcao.toString() + "\n");
		while (!pilhaMetodo.isEmpty()) {
			sb.append(pilhaMetodo.pop() + "\n");
		}
		return sb.toString();
	}
}