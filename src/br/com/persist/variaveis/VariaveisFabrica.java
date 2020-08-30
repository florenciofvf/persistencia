package br.com.persist.variaveis;

import br.com.persist.fabrica.AbstratoFabricaContainer;
import br.com.persist.fichario.FicharioAba;
import br.com.persist.principal.Formulario;

public class VariaveisFabrica extends AbstratoFabricaContainer {

	@Override
	public FicharioAba criarFicharioAba(Formulario formulario, String classeFabricaEContainerDetalhe) {
		return new VariaveisContainer(null, formulario);
	}
}