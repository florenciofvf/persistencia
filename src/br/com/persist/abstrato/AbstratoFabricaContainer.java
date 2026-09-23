package br.com.persist.abstrato;

import java.util.ArrayList;
import java.util.List;

import br.com.persist.componente.Menu;
import br.com.persist.fichario.PaginaServico;
import br.com.persist.formulario.Formulario;

public abstract class AbstratoFabricaContainer implements FabricaContainer {
	@Override
	public void processarMenu(Formulario formulario, Menu menu) {
		//
	}

	@Override
	public AbstratoConfiguracao getConfiguracao(Formulario formulario) {
		return null;
	}

	@Override
	public List<Servico> getServicos(Formulario formulario) {
		return new ArrayList<>();
	}

	@Override
	public PaginaServico getPaginaServico() {
		return null;
	}

	@Override
	public void inicializar() {
	}
}