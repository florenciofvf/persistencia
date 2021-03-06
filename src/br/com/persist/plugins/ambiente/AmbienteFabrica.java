package br.com.persist.plugins.ambiente;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import br.com.persist.abstrato.AbstratoFabricaContainer;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.MenuPadrao1;
import br.com.persist.fichario.Pagina;
import br.com.persist.fichario.PaginaServico;
import br.com.persist.formulario.Formulario;
import br.com.persist.plugins.ambiente.AmbienteContainer.Ambiente;

public class AmbienteFabrica extends AbstratoFabricaContainer {

	@Override
	public void inicializar() {
		Util.criarDiretorio(AmbienteConstantes.AMBIENTES);
	}

	@Override
	public PaginaServico getPaginaServico() {
		return new AmbientePaginaServico();
	}

	private class AmbientePaginaServico implements PaginaServico {
		@Override
		public Pagina criarPagina(Formulario formulario, String stringPersistencia) {
			Ambiente ambiente = Ambiente.get(stringPersistencia);
			return new AmbienteContainer(null, formulario, null, ambiente);
		}
	}

	@Override
	public List<JMenuItem> criarMenuItens(Formulario formulario, JMenu menu) {
		List<JMenuItem> lista = new ArrayList<>();
		for (AmbienteContainer.Ambiente ambiente : AmbienteContainer.Ambiente.values()) {
			lista.add(new MenuAmbiente(formulario, ambiente));
		}
		return lista;
	}

	private class MenuAmbiente extends MenuPadrao1 {
		private static final long serialVersionUID = 1L;

		private MenuAmbiente(Formulario formulario, AmbienteContainer.Ambiente ambiente) {
			super(Constantes.LABEL_VAZIO, null);
			setText(AmbienteMensagens.getString(ambiente.getChaveTitulo()));
			ficharioAcao.setActionListener(
					e -> formulario.adicionarPagina(new AmbienteContainer(null, formulario, null, ambiente)));
			formularioAcao.setActionListener(e -> AmbienteFormulario.criar(formulario, null, ambiente));
			dialogoAcao.setActionListener(e -> AmbienteDialogo.criar(formulario, ambiente));
		}
	}
}