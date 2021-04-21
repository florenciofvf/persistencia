package br.com.persist.plugins.update;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import br.com.persist.abstrato.AbstratoFabricaContainer;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Icones;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.MenuPadrao1;
import br.com.persist.fichario.Pagina;
import br.com.persist.fichario.PaginaServico;
import br.com.persist.formulario.Formulario;

public class UpdateFabrica extends AbstratoFabricaContainer {

	@Override
	public void inicializar() {
		Util.criarDiretorio(UpdateConstantes.ATUALIZACOES);
	}

	@Override
	public PaginaServico getPaginaServico() {
		return new UpdatePaginaServico();
	}

	private class UpdatePaginaServico implements PaginaServico {
		@Override
		public Pagina criarPagina(Formulario formulario, String stringPersistencia) {
			return new UpdateContainer(null, formulario, null, null);
		}
	}

	@Override
	public List<JMenuItem> criarMenuItens(Formulario formulario, JMenu menu) {
		List<JMenuItem> lista = new ArrayList<>();
		lista.add(new MenuUpdate(formulario));
		return lista;
	}

	private class MenuUpdate extends MenuPadrao1 {
		private static final long serialVersionUID = 1L;

		private MenuUpdate(Formulario formulario) {
			super(Constantes.LABEL_ATUALIZAR, Icones.UPDATE);
			ficharioAcao.setActionListener(
					e -> formulario.adicionarPagina(new UpdateContainer(null, formulario, null, null)));
			formularioAcao.setActionListener(e -> UpdateFormulario.criar(formulario, null, null));
			dialogoAcao.setActionListener(e -> UpdateDialogo.criar(formulario, null, null));
		}
	}
}