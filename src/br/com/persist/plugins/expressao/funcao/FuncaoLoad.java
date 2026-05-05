package br.com.persist.plugins.expressao.funcao;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.processador.Funcao;
import br.com.persist.plugins.expressao.processador.Instrucao;

public abstract class FuncaoLoad extends Instrucao {
	protected FuncaoLoad(int indice, String nome) throws ExpressaoException {
		super(indice, nome);
	}

	protected void checarTipo(boolean tipoVoid, Funcao funcao, String nomeBiblioteca, String nomeFuncao)
			throws ExpressaoException {
		if (tipoVoid != funcao.isTipoVoid()) {
			String invocacao = nomeBiblioteca + "." + nomeFuncao;
			throw new ExpressaoException("erro.invocacao.retorno", invocacao, (funcao.isTipoVoid() ? "VOID" : "VALOR"));
		}
	}
}