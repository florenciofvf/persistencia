package br.com.persist.ambiente;

import java.util.ArrayList;
import java.util.List;

import br.com.persist.ambiente.AmbienteContainer.Ambiente;
import br.com.persist.componente.Menu;
import br.com.persist.fabrica.AbstratoFabricaContainer;
import br.com.persist.fichario.FicharioAba;
import br.com.persist.principal.Formulario;
import br.com.persist.util.Constantes;
import br.com.persist.util.MenuPadrao1;

public class AmbienteFabrica extends AbstratoFabricaContainer {

	@Override
	public FicharioAba criarFicharioAba(Formulario formulario, String classeFabricaEContainerDetalhe) {
		int pos = classeFabricaEContainerDetalhe.indexOf(Constantes.U);
		String chaveAmbiente = classeFabricaEContainerDetalhe.substring(pos + 1);
		Ambiente ambiente = Ambiente.get(chaveAmbiente);
		return new AmbienteContainer(null, formulario, null, ambiente);
	}

	@Override
	public List<Menu> criarMenus(Formulario formulario) {
		List<Menu> lista = new ArrayList<>();

		for (AmbienteContainer.Ambiente ambiente : AmbienteContainer.Ambiente.values()) {
			lista.add(new MenuAmbiente(formulario, ambiente));
		}

		return lista;
	}

	private class MenuAmbiente extends MenuPadrao1 {
		private static final long serialVersionUID = 1L;

		private MenuAmbiente(Formulario formulario, AmbienteContainer.Ambiente ambiente) {
			super(ambiente.getChaveRotulo(), null);

			ficharioAcao.setActionListener(
					e -> formulario.adicionarFicharioAba(new AmbienteContainer(null, formulario, null, ambiente)));
			formularioAcao.setActionListener(e -> AmbienteFormulario.criar(formulario, Constantes.VAZIO, ambiente));
			dialogoAcao.setActionListener(e -> AmbienteDialogo.criar(formulario, ambiente));
		}
	}
}