package br.com.persist.anexo;

import br.com.persist.fabrica.FabricaContainer;
import br.com.persist.fichario.FicharioAba;
import br.com.persist.principal.Formulario;

public class AnexoTreeFabrica implements FabricaContainer {

	@Override
	public FicharioAba criarFicharioAba(Formulario formulario, String classeFabricaEContainerDetalhe) {
		return new AnexoTreeContainer(null, formulario);
	}
}