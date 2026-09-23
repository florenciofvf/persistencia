package br.com.persist.plugins.objeto;

import br.com.persist.abstrato.AbstratoFabricaContainer;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Icones;
import br.com.persist.componente.Menu;
import br.com.persist.componente.MenuPadrao1;
import br.com.persist.fichario.Pagina;
import br.com.persist.fichario.PaginaServico;
import br.com.persist.formulario.Formulario;

public class DesktopFabrica extends AbstratoFabricaContainer {
	@Override
	public PaginaServico getPaginaServico() {
		return new DesktopPaginaServico();
	}

	private class DesktopPaginaServico implements PaginaServico {
		@Override
		public Pagina criarPagina(Formulario formulario, String stringPersistencia) {
			return new Desktop(formulario, false);
		}
	}

	@Override
	public void processarMenu(Formulario formulario, Menu menu) {
		menu.add(new MenuDesktop(formulario));
	}

	private class MenuDesktop extends MenuPadrao1 {
		private static final long serialVersionUID = 1L;

		private MenuDesktop(Formulario formulario) {
			super(Constantes.LABEL_DESKTOP, Icones.PANEL2);
			ficharioAcao.setActionListener(e -> formulario.adicionarPagina(new Desktop(formulario, false)));
			formularioAcao.setActionListener(e -> DesktopFormulario.criar(formulario));
			dialogoAcao.setActionListener(e -> DesktopDialogo.criar(formulario));
		}
	}
}