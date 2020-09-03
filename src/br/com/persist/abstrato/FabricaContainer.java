package br.com.persist.abstrato;

import java.util.List;

import javax.swing.JMenuItem;

import br.com.persist.fichario.PaginaServico;
import br.com.persist.principal.Formulario;

public interface FabricaContainer {
	public List<Servico> getServicos(Formulario formulario);

	public List<JMenuItem> criarMenuItens(Formulario formulario);

	public PaginaServico getPaginaServico();
}