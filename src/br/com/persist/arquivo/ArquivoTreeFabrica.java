package br.com.persist.arquivo;

import br.com.persist.fabrica.FabricaContainer;
import br.com.persist.fichario.FicharioAba;
import br.com.persist.principal.Formulario;

public class ArquivoTreeFabrica implements FabricaContainer {

	@Override
	public FicharioAba criarFicharioAba(Formulario formulario, String classeFabricaEContainerDetalhe) {
		return new ArquivoTreeContainer(null, formulario);
	}
}