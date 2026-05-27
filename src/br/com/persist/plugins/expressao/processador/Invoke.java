package br.com.persist.plugins.expressao.processador;

import br.com.persist.plugins.expressao.ExpressaoException;

public interface Invoke {
	default void validar(Funcao funcao, boolean comRetorno) throws ExpressaoException {
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