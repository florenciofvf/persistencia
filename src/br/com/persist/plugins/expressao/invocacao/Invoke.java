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

	protected void validar(Funcao funcao, boolean comRetorno) throws ExpressaoException {
		if (funcao == null) {
			throw new ExpressaoException("Funcao nula.", false);
		}
		if (comRetorno && funcao.isTipoVoid()) {
			throw new ExpressaoException("erro.funcao_sem_retorno", funcao.getNome(),
					funcao.getBiblioteca().getNomeAbsoluto());
		} else if (!comRetorno && !funcao.isTipoVoid()) {
			throw new ExpressaoException("erro.funcao_com_retorno", funcao.getNome(),
					funcao.getBiblioteca().getNomeAbsoluto());
		}
	}
}