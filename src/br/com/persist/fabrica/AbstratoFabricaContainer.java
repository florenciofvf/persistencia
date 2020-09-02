package br.com.persist.fabrica;

import java.util.ArrayList;
import java.util.List;

import br.com.persist.componente.Menu;
import br.com.persist.fichario.FicharioServico;
import br.com.persist.principal.Formulario;
import br.com.persist.servico.Servico;

public class AbstratoFabricaContainer implements FabricaContainer {

	@Override
	public List<Servico> getServicos(Formulario formulario) {
		return new ArrayList<>();
	}

	@Override
	public List<Menu> criarMenus(Formulario formulario) {
		return new ArrayList<>();
	}

	@Override
	public FicharioServico getFicharioServico() {
		return null;
	}
}