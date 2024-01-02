package br.com.persist.fichario;

import br.com.persist.formulario.Formulario;

@FunctionalInterface
public interface PaginaServico {
	public Pagina criarPagina(Formulario formulario, String stringPersistencia);
}