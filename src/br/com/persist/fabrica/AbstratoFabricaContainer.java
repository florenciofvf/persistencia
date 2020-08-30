package br.com.persist.fabrica;

import java.util.ArrayList;
import java.util.List;

import br.com.persist.componente.Menu;
import br.com.persist.principal.Formulario;

public abstract class AbstratoFabricaContainer implements FabricaContainer {

	@Override
	public List<Menu> criarMenus(Formulario formulario) {
		return new ArrayList<>();
	}
}