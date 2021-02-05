package br.com.persist.abstrato;

import java.util.List;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import br.com.persist.fichario.PaginaServico;
import br.com.persist.formulario.Formulario;

public interface FabricaContainer {
	public List<JMenuItem> criarMenuItens(Formulario formulario, JMenu menu);

	public List<Servico> getServicos(Formulario formulario);

	public PaginaServico getPaginaServico();

	public void inicializar();
}