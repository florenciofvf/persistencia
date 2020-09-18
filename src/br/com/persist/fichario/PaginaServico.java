package br.com.persist.fichario;

import br.com.persist.formulario.Formulario;

public interface PaginaServico {
	public Pagina criarPagina(Formulario formulario, String stringPersistencia);
}