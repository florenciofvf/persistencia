package br.com.persist.fragmento;

import br.com.persist.fabrica.AbstratoFabricaContainer;
import br.com.persist.fichario.FicharioAba;
import br.com.persist.principal.Formulario;

public class FragmentoFabrica extends AbstratoFabricaContainer {

	@Override
	public FicharioAba criarFicharioAba(Formulario formulario, String classeFabricaEContainerDetalhe) {
		return new FragmentoContainer(null, formulario, null);
	}
}