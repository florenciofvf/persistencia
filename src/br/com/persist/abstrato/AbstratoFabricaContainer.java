package br.com.persist.abstrato;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import br.com.persist.fichario.PaginaServico;
import br.com.persist.formulario.Formulario;

public abstract class AbstratoFabricaContainer implements FabricaContainer {
	@Override
	public List<JMenuItem> criarMenuItens(Formulario formulario, JMenu menu) {
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

	@Override
	public void inicializar() {
	}
}