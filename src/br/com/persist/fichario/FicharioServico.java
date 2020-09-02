package br.com.persist.fichario;

import br.com.persist.fabrica.FabricaContainer;
import br.com.persist.principal.Formulario;

public interface FicharioServico {
	public Pagina criarPagina(Formulario formulario, String string);

	public FabricaContainer getFabricaContainer();
}