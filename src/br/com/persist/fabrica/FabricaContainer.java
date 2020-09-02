package br.com.persist.fabrica;

import java.util.List;

import javax.swing.JMenuItem;

import br.com.persist.fichario.PaginaServico;
import br.com.persist.principal.Formulario;
import br.com.persist.servico.Servico;

public interface FabricaContainer {
	public List<Servico> getServicos(Formulario formulario);

	public List<JMenuItem> criarMenuItens(Formulario formulario);

	public PaginaServico getPaginaServico();
}