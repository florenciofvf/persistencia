package br.com.persist.plugins.expressao.biblioteca;

import java.util.Map;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.organiza.AliasContexto;

public interface LinkBibliotecaContexto {
	void processarChave(String chamada, String[] array);

	void processarChave2(String chamada, String[] array, Map<String, AliasContexto> mapaAlias, CacheBiblioteca cache)
			throws ExpressaoException;

	void processarChaveN(String chamada, String[] array, CacheBiblioteca cache) throws ExpressaoException;
}