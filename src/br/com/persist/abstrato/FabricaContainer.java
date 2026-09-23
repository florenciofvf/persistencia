package br.com.persist.abstrato;

import java.util.List;

import br.com.persist.componente.Menu;
import br.com.persist.fichario.PaginaServico;
import br.com.persist.formulario.Formulario;

public interface FabricaContainer {
	public AbstratoConfiguracao getConfiguracao(Formulario formulario);

	public void processarMenu(Formulario formulario, Menu menu);

	public List<Servico> getServicos(Formulario formulario);

	public PaginaServico getPaginaServico();

	public void inicializar();
}