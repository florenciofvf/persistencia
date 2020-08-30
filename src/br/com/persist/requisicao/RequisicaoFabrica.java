package br.com.persist.requisicao;

import br.com.persist.fabrica.AbstratoFabricaContainer;
import br.com.persist.fichario.FicharioAba;
import br.com.persist.principal.Formulario;

public class RequisicaoFabrica extends AbstratoFabricaContainer {

	@Override
	public FicharioAba criarFicharioAba(Formulario formulario, String classeFabricaEContainerDetalhe) {
		return new RequisicaoContainer(null, formulario, null, null);
	}
}