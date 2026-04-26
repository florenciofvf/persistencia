package br.com.persist.plugins.expressao.salto;

import br.com.persist.plugins.expressao.ExpressaoException;

public interface CheckSalto {
	public void checkDestino() throws ExpressaoException;

	public void setDispensavel(boolean dispensavel);

	public boolean isDispensavel();
}