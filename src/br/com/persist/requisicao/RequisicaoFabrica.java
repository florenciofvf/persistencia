package br.com.persist.requisicao;

import br.com.persist.fabrica.FabricaContainer;
import br.com.persist.fichario.FicharioAba;
import br.com.persist.principal.Formulario;

public class RequisicaoFabrica implements FabricaContainer {

	@Override
	public FicharioAba criarFicharioAba(Formulario formulario, String classeFabricaEContainerDetalhe) {
		return new RequisicaoContainer(null, formulario, null, null);
	}
}