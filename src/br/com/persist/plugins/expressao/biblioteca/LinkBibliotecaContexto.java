package br.com.persist.plugins.expressao.biblioteca;

import java.util.Map;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.organiza.AliasContexto;

public interface LinkBibliotecaContexto {
	public void configurarLinkBibliotecaPre(Map<String, AliasContexto> mapaAlias) throws ExpressaoException;

	public void initLink();
}