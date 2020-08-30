package br.com.persist.fabrica;

import java.util.List;

import br.com.persist.componente.Menu;
import br.com.persist.fichario.FicharioAba;
import br.com.persist.principal.Formulario;

public interface FabricaContainer {
	public FicharioAba criarFicharioAba(Formulario formulario, String classeFabricaEContainerDetalhe);

	public List<Menu> criarMenus(Formulario formulario);
}