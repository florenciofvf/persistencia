package br.com.persist.fabrica;

import br.com.persist.fichario.FicharioAba;
import br.com.persist.principal.Formulario;

public interface FabricaContainer {
	public FicharioAba criarFicharioAba(Formulario formulario, String classeFabricaEContainerDetalhe);
}