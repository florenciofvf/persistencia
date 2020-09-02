package br.com.persist.fabrica;

import java.util.List;

import br.com.persist.componente.Menu;
import br.com.persist.fichario.FicharioServico;
import br.com.persist.principal.Formulario;
import br.com.persist.servico.Servico;

public interface FabricaContainer {
	public List<Servico> getServicos(Formulario formulario);

	public List<Menu> criarMenus(Formulario formulario);

	public FicharioServico getFicharioServico();
}