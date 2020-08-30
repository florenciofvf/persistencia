package br.com.persist.variaveis;

import br.com.persist.fabrica.FabricaContainer;
import br.com.persist.fichario.FicharioAba;
import br.com.persist.principal.Formulario;

public class VariaveisFabrica implements FabricaContainer {

	@Override
	public FicharioAba criarFicharioAba(Formulario formulario, String classeFabricaEContainerDetalhe) {
		return new VariaveisContainer(null, formulario);
	}
}