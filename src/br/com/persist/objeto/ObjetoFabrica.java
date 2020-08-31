package br.com.persist.objeto;

import br.com.persist.fabrica.AbstratoFabricaContainer;
import br.com.persist.fichario.FicharioAba;
import br.com.persist.principal.Formulario;

public class ObjetoFabrica extends AbstratoFabricaContainer {

	@Override
	public FicharioAba criarFicharioAba(Formulario formulario, String classeFabricaEContainerDetalhe) {
		ObjetoContainer container = new ObjetoContainer(formulario, null);
		container.setAbortarFecharComESCSuperficie(true);
		container.estadoSelecao();
		return container;
	}
}