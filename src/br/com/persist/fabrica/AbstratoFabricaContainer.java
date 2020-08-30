package br.com.persist.fabrica;

import br.com.persist.componente.Menu;

public abstract class AbstratoFabricaContainer implements FabricaContainer {

	@Override
	public Menu criarMenu() {
		return null;
	}
}