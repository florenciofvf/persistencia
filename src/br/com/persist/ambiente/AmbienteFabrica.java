package br.com.persist.ambiente;

import br.com.persist.ambiente.AmbienteContainer.Ambiente;
import br.com.persist.fabrica.FabricaContainer;
import br.com.persist.fichario.FicharioAba;
import br.com.persist.principal.Formulario;
import br.com.persist.util.Constantes;

public class AmbienteFabrica implements FabricaContainer {

	@Override
	public FicharioAba criarFicharioAba(Formulario formulario, String classeFabricaEContainerDetalhe) {
		int pos = classeFabricaEContainerDetalhe.indexOf(Constantes.U);
		String chaveAmbiente = classeFabricaEContainerDetalhe.substring(pos + 1);
		Ambiente ambiente = Ambiente.get(chaveAmbiente);
		return new AmbienteContainer(null, formulario, null, ambiente);
	}
}