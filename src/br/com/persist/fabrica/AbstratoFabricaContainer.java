package br.com.persist.fabrica;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JMenuItem;

import br.com.persist.fichario.PaginaServico;
import br.com.persist.principal.Formulario;
import br.com.persist.servico.Servico;

public class AbstratoFabricaContainer implements FabricaContainer {
	@Override
	public List<JMenuItem> criarMenuItens(Formulario formulario) {
		return new ArrayList<>();
	}

	@Override
	public List<Servico> getServicos(Formulario formulario) {
		return new ArrayList<>();
	}

	@Override
	public PaginaServico getPaginaServico() {
		return null;
	}
}